package io.github.tunnelvisionmod.tunnelvision.features.mining

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ChatReceivedEvent
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.DisconnectEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.features.mining.CooldownSync.TICKS_PER_SECOND
import io.github.tunnelvisionmod.tunnelvision.hud.HudManager
import io.github.tunnelvisionmod.tunnelvision.hud.HudPosition
import io.github.tunnelvisionmod.tunnelvision.hud.HudWidget
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.TabList
import io.github.tunnelvisionmod.tunnelvision.utils.loreLines
import net.minecraft.ChatFormatting
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents

object PickaxeAbility {
	private val config get() = ConfigManager.config.general.pickaxeAbility

	private var ability: String? = null
	private var ticksLeft = 0
	private var lastMiningToolLore: List<String>? = null

	fun init() {
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<ChatReceivedEvent> { onChat(it) }
		EventBus.on<DisconnectEvent> { reset() }
		EventBus.on<LocationChangedEvent> { ticksLeft = 0 }
		HudManager.register(Widget)
	}

	private fun onTick() {
		if (!config.enabled || !SkyBlock.isOnSkyBlock) return
		rememberHeldMiningTool()
		PickaxeAbilityParser.parseTab(TabList.lines)?.let { syncWithTab(it) }
		if (ticksLeft > 0 && --ticksLeft == 0) onReady()
	}

	private fun syncWithTab(tab: TabAbility) {
		ability = tab.name
		val seconds = tab.secondsLeft
		if (seconds == null) {
			if (ticksLeft > 1) {
				ticksLeft = CooldownSync.onAvailable(ticksLeft)
				Debug.log { "PickaxeAbility: ${tab.name} available early via tab, ${if (ticksLeft == 0) "reset silently" else "ready"}" }
			}
			return
		}
		val synced = CooldownSync.resync(ticksLeft, seconds) ?: return
		Debug.log { "PickaxeAbility: Cooldown(${tab.name}, ${seconds}s) via tab" }
		ticksLeft = synced
	}

	private fun onChat(event: ChatReceivedEvent) {
		if (!config.enabled || !SkyBlock.isOnSkyBlock) return
		val name = PickaxeAbilityParser.parseUsedMessage(event.text) ?: return
		ability = name
		if (PickaxeAbilityParser.parseTab(TabList.lines) != null) return
		val lore = heldMiningToolLore() ?: lastMiningToolLore ?: return
		val seconds = PickaxeAbilityParser.parseLoreCooldown(lore) ?: return
		Debug.log { "PickaxeAbility: Cooldown($name, ${seconds}s) via lore" }
		ticksLeft = seconds * TICKS_PER_SECOND
	}

	private fun onReady() {
		val name = ability ?: return
		Debug.log { "PickaxeAbility: $name ready" }
		if (config.showTitle) {
			mc.gui.setTimes(0, 50, 10)
			mc.gui.setTitle(Component.literal("${name.uppercase()}!").withStyle(ChatFormatting.GOLD))
		}
		if (config.playSound) {
			mc.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.EXPERIENCE_ORB_PICKUP, 1f))
		}
	}

	private fun heldMiningToolLore(): List<String>? =
		mc.player?.mainHandItem?.loreLines()?.takeIf { PickaxeAbilityParser.isMiningTool(it) }

	private fun rememberHeldMiningTool() {
		heldMiningToolLore()?.let { lastMiningToolLore = it }
	}

	private fun reset() {
		ability = null
		ticksLeft = 0
		lastMiningToolLore = null
	}

	private fun cooldownLine(name: String, seconds: Int): Component =
		Component.literal("$name: ").withStyle(ChatFormatting.GOLD)
			.append(Component.literal("${seconds}s").withStyle(ChatFormatting.YELLOW))

	object Widget : HudWidget("pickaxe_ability_timer", "Pickaxe Ability Timer", HudPosition(0.02f, 0.4f)) {
		override val isEnabled get() = config.enabled && config.showWidget

		override fun getLines(): List<Component> {
			val name = ability ?: return emptyList()
			if (ticksLeft <= 0) return emptyList()
			return listOf(cooldownLine(name, (ticksLeft + TICKS_PER_SECOND - 1) / TICKS_PER_SECOND))
		}

		override fun getExampleLines() = listOf(cooldownLine("Mining Speed Boost", 42))
	}
}
