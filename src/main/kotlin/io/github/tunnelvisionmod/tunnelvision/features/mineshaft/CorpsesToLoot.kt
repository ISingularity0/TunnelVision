package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.features.crystals.CrystalNotifications
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftDetection
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftType
import io.github.tunnelvisionmod.tunnelvision.hud.HudManager
import io.github.tunnelvisionmod.tunnelvision.hud.HudPosition
import io.github.tunnelvisionmod.tunnelvision.hud.HudWidget
import io.github.tunnelvisionmod.tunnelvision.utils.Bazaar
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.TabList
import io.github.tunnelvisionmod.tunnelvision.utils.plainName
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.player.Inventory

object CorpsesToLoot {
	private val config get() = ConfigManager.config.mineshaft.corpsesToLoot
	private val valueConfig get() = ConfigManager.config.mineshaft.mineshaftValue

	fun init() {
		EventBus.on<ClientTickEvent> { if (config.enabled && SkyBlock.isOnMiningIsland) Bazaar.refreshIfStale() }
		HudManager.register(Widget)
	}

	private fun currentRule(): LootRule = CorpseLoot.rule(
		lapisOnly = valueConfig.lapisOnly,
		crystalsFull = CrystalNotifications.crystalsAndForgeFull,
		shouldMine = MineshaftValueAlert.currentVerdict()?.shouldMine,
	)

	private fun carriedItemNames(): Set<String> {
		val inventory = mc.player?.inventory ?: return emptySet()
		return (0 until Inventory.INVENTORY_SIZE).map { inventory.getItem(it).plainName() }.toSet()
	}

	private fun CorpseType.color(): ChatFormatting = when (this) {
		CorpseType.LAPIS -> ChatFormatting.BLUE
		CorpseType.UMBER -> ChatFormatting.GOLD
		CorpseType.TUNGSTEN -> ChatFormatting.GRAY
		CorpseType.VANGUARD -> ChatFormatting.AQUA
	}

	private fun header(rule: LootRule, vanguardShaft: Boolean): Component =
		Component.literal("Loot: ").withStyle(ChatFormatting.GOLD).append(Component.literal(rule.label(vanguardShaft)).withStyle(ChatFormatting.WHITE))

	private val fossilLine: Component = Component.literal("Mine the Fossil").withStyle(ChatFormatting.LIGHT_PURPLE)

	private fun corpseLine(type: CorpseType, count: Int, hasKey: Boolean): Component {
		val line = Component.literal("${type.tabName} ×$count").withStyle(type.color())
		return if (hasKey) line else line.append(Component.literal(" · no ${type.keyName}").withStyle(ChatFormatting.RED))
	}

	object Widget : HudWidget("corpses_to_loot", "Mineshaft To-Do", HudPosition(0.02f, 0.7f)) {
		override val isEnabled get() = config.enabled

		override fun getLines(): List<Component> {
			if (!SkyBlock.isInMineshaft) return emptyList()
			val lines = mutableListOf<Component>()
			CorpseLoot.parseUnlooted(TabList.lines)?.let { unlooted ->
				val rule = currentRule()
				val toLoot = CorpseLoot.toLoot(unlooted, rule)
				if (toLoot.isEmpty()) return@let
				val items = carriedItemNames()
				lines += header(rule, MineshaftDetection.type == MineshaftType.VANGUARD)
				lines += toLoot.entries.sortedBy { it.key.ordinal }.map { (type, count) ->
					corpseLine(type, count, hasKey = type.keyName == null || type.keyName in items)
				}
			}
			if (MineshaftWaypoints.hasPendingFossil) lines += fossilLine
			return lines
		}

		override fun getExampleLines() = listOf(
			header(LootRule.ALL, vanguardShaft = false),
			corpseLine(CorpseType.LAPIS, 2, hasKey = true),
			corpseLine(CorpseType.UMBER, 1, hasKey = false),
			fossilLine,
		)
	}
}
