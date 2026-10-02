package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.WorldRenderEvent
import io.github.tunnelvisionmod.tunnelvision.utils.ChatUtils
import io.github.tunnelvisionmod.tunnelvision.utils.render.WorldRender
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderContext
import net.minecraft.ChatFormatting
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.network.chat.Component
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import kotlin.math.roundToInt

object RouteRunner {
	private const val REACH = 4.5
	private const val CROSSHAIR_DISTANCE = 2.0
	private const val CHECK_EVERY_TICKS = 4
	private const val TARGET_COLOR = 0xFF55FF55.toInt()
	private const val FILL_ALPHA = 0x50

	private var waypoints: List<RouteWaypoint> = emptyList()
	private var index = 0
	private var ticks = 0
	private var isActive: () -> Boolean = { false }

	val isRunning: Boolean get() = waypoints.isNotEmpty()

	fun init() {
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<WorldRenderEvent> { onRender(it.context) }
	}

	fun start(waypoints: List<RouteWaypoint>, isActive: () -> Boolean) {
		this.waypoints = waypoints
		this.isActive = isActive
		index = 0
	}

	fun stop() {
		waypoints = emptyList()
		index = 0
	}

	fun step(by: Int) {
		if (isRunning) index = (index + by).coerceIn(0, waypoints.size)
	}

	private fun onTick() {
		if (!isRunning || !isActive() || ++ticks % CHECK_EVERY_TICKS != 0) return
		val level = mc.level ?: return
		val eye = mc.player?.eyePosition ?: return
		while (index < waypoints.size) {
			val remaining = waypoints[index].remaining(level)
			if (remaining.isNotEmpty() && !remaining.within(eye, REACH)) break
			if (++index == waypoints.size) ChatUtils.send(Component.literal("Route done").withStyle(ChatFormatting.GRAY))
		}
	}

	private fun onRender(context: LevelRenderContext) {
		if (!isRunning || !isActive()) return
		val player = mc.player ?: return
		val target = waypoints.getOrNull(index) ?: return
		val box = target.centre.blockBox()
		WorldRender.filled(context, box, TARGET_COLOR.withAlpha(FILL_ALPHA))
		WorldRender.outline(context, box, TARGET_COLOR, throughWalls = true)
		val crosshair = mc.gameRenderer.mainCamera.position().add(player.getViewVector(1f).scale(CROSSHAIR_DISTANCE))
		WorldRender.lines(context, listOf(crosshair to box.center), TARGET_COLOR, throughWalls = true)
		val labelPos = Vec3(box.center.x, box.maxY + 0.5, box.center.z)
		val text = "#${index + 1}/${waypoints.size} · ${target.blocks.size} gems · ${player.eyePosition.distanceTo(labelPos).roundToInt()}m"
		WorldRender.label(context, labelPos, Component.literal(text), TARGET_COLOR)
	}

	private fun RouteWaypoint.remaining(level: ClientLevel) = blocks.filter { !level.getBlockState(it.toBlockPos()).isAir }
	private fun List<Pos>.within(eye: Vec3, range: Double) = any { AABB(it.toBlockPos()).distanceToSqr(eye) <= range * range }
	private fun Pos.blockBox() = AABB(x.toDouble(), y.toDouble(), z.toDouble(), x + 1.0, y + 1.0, z + 1.0)
	private fun Pos.toBlockPos() = BlockPos(x, y, z)
	private fun Int.withAlpha(alpha: Int) = (this and 0x00FFFFFF) or (alpha shl 24)
}
