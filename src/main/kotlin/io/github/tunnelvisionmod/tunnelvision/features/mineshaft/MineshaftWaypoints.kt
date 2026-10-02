package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ChatReceivedEvent
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.events.WorldRenderEvent
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftDetection
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.Visibility
import io.github.tunnelvisionmod.tunnelvision.utils.render.WorldRender
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.level.block.Block
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.roundToInt

object MineshaftWaypoints {
	private const val CHECK_EVERY_TICKS = 5
	private const val SPOT_RANGE = 40.0
	private const val CLOSE_ENOUGH = 4.0
	private const val CORPSE_AT_SPOT = 3.0
	private const val VISIBLE_CHECKS_NEEDED = 2
	private const val FOSSIL_RADIUS = 12

	private const val SPOT_COLOR = 0xFFFFFFFF.toInt()
	private const val FOSSIL_COLOR = 0xFFB040FF.toInt()
	private const val FILL_ALPHA = 0x50

	private val lootMessage = Regex("""^(LAPIS|UMBER|TUNGSTEN|VANGUARD) CORPSE LOOT!""")

	private val config get() = ConfigManager.config.mineshaft.mineshaftWaypoints
	private val spots by lazy { MineshaftSpots.load() }
	private val state = WaypointState()

	val hasPendingFossil: Boolean get() = config.enabled && started && state.fossil != null && !fossilStarted
	private val visibleChecks = mutableMapOf<Pos, Int>()
	private val isQuartz = mutableMapOf<Block, Boolean>()

	private var started = false
	private var todosDone = false
	private var ticks = 0
	private var fossilBlocks: Set<BlockPos> = emptySet()
	private var fossilEdges: List<Pair<Vec3, Vec3>> = emptyList()
	private var fossilStarted = false

	fun init() {
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<ChatReceivedEvent> { onChat(it) }
		EventBus.on<LocationChangedEvent> { reset() }
		EventBus.on<WorldRenderEvent> { onRender(it.context) }
	}

	private fun onTick() {
		if (!config.enabled || !SkyBlock.isInMineshaft) return
		val level = mc.level ?: return
		if (!started) start() else if (++ticks % CHECK_EVERY_TICKS == 0) {
			updateTodosDone()
			if (!todosDone) {
				checkSpots(level)
				checkCorpses(level)
				checkAllCorpsesFound()
			}
			checkFossil(level)
		}
	}

	private fun start() {
		val type = MineshaftDetection.type ?: return
		started = true
		state.start(
			if (config.corpseSpots) spots.corpseSpots(type) else emptyList(),
			if (config.fossil) spots.fossil(type) else null,
		)
		Debug.log { "MineshaftWaypoints: ${type.code} with ${state.possibleSpots.size} spots, fossil ${state.fossil}" }
	}

	private fun updateTodosDone() {
		val done = CorpsesToLoot.todosDone
		if (done != todosDone) Debug.log { "MineshaftWaypoints: to-dos ${if (done) "done, hiding corpse waypoints" else "open again"}" }
		todosDone = done
	}

	private fun checkSpots(level: ClientLevel) {
		val eye = mc.player?.eyePosition ?: return
		for (spot in state.possibleSpots.toList()) {
			val centre = spot.centre()
			val distance = eye.distanceTo(centre)
			if (distance > SPOT_RANGE) continue
			val visible = distance <= CLOSE_ENOUGH || Visibility.canSeeAny(spot.samples())
			val count = if (visible) (visibleChecks[spot] ?: 0) + 1 else 0
			visibleChecks[spot] = count
			if (count < VISIBLE_CHECKS_NEEDED) continue
			val corpse = corpseStands(level).firstOrNull { it.position().distanceTo(centre) <= CORPSE_AT_SPOT }
			Debug.log { "MineshaftWaypoints: checked spot $spot -> ${corpse?.corpseType() ?: "empty"}" }
			state.onSpotSeen(spot, corpse?.let { CorpseWaypoint(it.corpseType()!!, it.blockPosition().toPos()) })
		}
	}

	private fun checkCorpses(level: ClientLevel) {
		val eye = mc.player?.eyePosition ?: return
		for (stand in corpseStands(level)) {
			if (stand.position().distanceTo(eye) > SPOT_RANGE) continue
			if (!Visibility.canSeeAny(listOf(stand.position().add(0.0, 1.0, 0.0), stand.eyePosition))) continue
			state.onCorpseSeen(CorpseWaypoint(stand.corpseType()!!, stand.blockPosition().toPos()))
		}
	}

	private fun checkAllCorpsesFound() {
		if (state.possibleSpots.isEmpty()) return
		state.onCorpseTotal(MineshaftDetection.corpseCount)
		if (state.possibleSpots.isEmpty()) Debug.log { "MineshaftWaypoints: all ${MineshaftDetection.corpseCount} corpses found, cleared remaining spots" }
	}

	private fun checkFossil(level: ClientLevel) {
		val fossil = state.fossil ?: return
		val centre = fossil.toBlockPos()
		if (listOf(-FOSSIL_RADIUS, FOSSIL_RADIUS).any { dx -> listOf(-FOSSIL_RADIUS, FOSSIL_RADIUS).any { dz -> !level.isLoaded(centre.offset(dx, 0, dz)) } }) return
		if (fossilBlocks.isEmpty()) {
			fossilBlocks = findFossil(level, centre)
			fossilEdges = fossilBlocks.outline()
			if (fossilBlocks.isNotEmpty()) Debug.log { "MineshaftWaypoints: fossil has ${fossilBlocks.size} blocks" }
			return
		}
		val left = fossilBlocks.filter { level.getBlockState(it).block.isQuartz() }.toSet()
		if (left.size == fossilBlocks.size) return
		if (!fossilStarted) Debug.log { "MineshaftWaypoints: started mining the fossil" }
		fossilStarted = true
		fossilBlocks = left
		fossilEdges = left.outline()
		if (left.isEmpty()) {
			Debug.log { "MineshaftWaypoints: fossil mined" }
			state.onFossilBlockMined(fossil)
			fossilStarted = false
		}
	}

	private fun Set<BlockPos>.outline() = VeinOutline.edges(map { it.toPos() }.toSet()).map { (a, b) -> a.toVec3() to b.toVec3() }

	private fun findFossil(level: ClientLevel, centre: BlockPos): Set<BlockPos> =
		BlockPos.betweenClosed(centre.offset(-FOSSIL_RADIUS, -FOSSIL_RADIUS, -FOSSIL_RADIUS), centre.offset(FOSSIL_RADIUS, FOSSIL_RADIUS, FOSSIL_RADIUS))
			.map { it.immutable() }
			.filter { it.closerThan(centre, FOSSIL_RADIUS.toDouble()) && level.getBlockState(it).block.isQuartz() }
			.toSet()

	private fun onChat(event: ChatReceivedEvent) {
		if (!config.enabled || !SkyBlock.isInMineshaft) return
		val type = lootMessage.find(event.text.trim())?.groupValues?.get(1)?.let { CorpseType.valueOf(it) } ?: return
		val player = mc.player?.blockPosition()?.toPos() ?: return
		Debug.log { "MineshaftWaypoints: looted $type" }
		state.onLooted(type, player)
	}

	private fun onRender(context: LevelRenderContext) {
		if (!config.enabled || !SkyBlock.isInMineshaft || !started) return
		val eye = mc.player?.eyePosition ?: return
		if (!todosDone) renderCorpses(context, eye)
		state.fossil?.let { fossil ->
			WorldRender.lines(context, fossilEdges, FOSSIL_COLOR, throughWalls = false)
			if (fossilStarted) return@let
			val box = AABB(fossil.toBlockPos())
			WorldRender.outline(context, box, FOSSIL_COLOR, throughWalls = true)
			label(context, box, "Fossil", FOSSIL_COLOR, eye)
		}
	}

	private fun renderCorpses(context: LevelRenderContext, eye: Vec3) {
		for (spot in state.possibleSpots) {
			val box = spot.box()
			WorldRender.outline(context, box, SPOT_COLOR, throughWalls = true)
			label(context, box, "Possible Corpse", SPOT_COLOR, eye)
		}
		for (corpse in state.corpses) {
			val box = corpse.pos.box()
			val color = corpse.type.color()
			WorldRender.filled(context, box, color.withAlpha(FILL_ALPHA))
			WorldRender.outline(context, box, color, throughWalls = true)
			label(context, box, "${corpse.type.displayName()} Corpse", color, eye)
		}
	}

	private fun label(context: LevelRenderContext, box: AABB, name: String, color: Int, eye: Vec3) {
		if (!config.labels) return
		val pos = Vec3(box.center.x, box.maxY + 0.5, box.center.z)
		WorldRender.label(context, pos, Component.literal("$name · ${eye.distanceTo(pos).roundToInt()}m"), color)
	}

	private fun corpseStands(level: ClientLevel): List<ArmorStand> =
		level.entitiesForRendering().filterIsInstance<ArmorStand>().filter { it.corpseType() != null }

	private fun reset() {
		started = false
		todosDone = false
		ticks = 0
		state.reset()
		visibleChecks.clear()
		fossilBlocks = emptySet()
		fossilEdges = emptyList()
		fossilStarted = false
	}

	private fun Block.isQuartz(): Boolean = isQuartz.getOrPut(this) {
		val path = BuiltInRegistries.BLOCK.getKey(this).path
		"quartz" in path && "ore" !in path
	}

	private fun Pos.toBlockPos() = BlockPos(x, y, z)
	private fun BlockPos.toPos() = Pos(x, y, z)
	private fun Pos.toVec3() = Vec3(x.toDouble(), y.toDouble(), z.toDouble())
	private fun Pos.centre() = Vec3(x + 0.5, y + 1.0, z + 0.5)
	private fun Pos.box() = AABB(x.toDouble(), y.toDouble(), z.toDouble(), x + 1.0, y + 2.0, z + 1.0)
	private fun Pos.samples() = listOf(centre(), Vec3(x + 0.5, y + 0.2, z + 0.5), Vec3(x + 0.5, y + 1.8, z + 0.5))

	private fun Int.withAlpha(alpha: Int) = (this and 0x00FFFFFF) or (alpha shl 24)

	private fun CorpseType.color(): Int = when (this) {
		CorpseType.LAPIS -> 0xFF5555FF.toInt()
		CorpseType.UMBER -> 0xFFFFAA00.toInt()
		CorpseType.TUNGSTEN -> 0xFFAAAAAA.toInt()
		CorpseType.VANGUARD -> 0xFF55FFFF.toInt()
	}

	private fun CorpseType.displayName(): String = name.lowercase().replaceFirstChar { it.uppercase() }
}
