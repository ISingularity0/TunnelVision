package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ChatReceivedEvent
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.features.party.PartyChatParser
import io.github.tunnelvisionmod.tunnelvision.features.party.PartyCommands
import io.github.tunnelvisionmod.tunnelvision.utils.ChatUtils
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.KeyUtils
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import net.minecraft.ChatFormatting
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents

object SharedMineshaftWarp {
	private val config get() = ConfigManager.config.mineshaft.sharedMineshaftWarp
	private val window = WarpWindow()

	fun init() {
		EventBus.on<ChatReceivedEvent> { onChat(it) }
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<LocationChangedEvent> { window.close() }
	}

	private fun onChat(event: ChatReceivedEvent) {
		if (!config.enabled || !SkyBlock.isOnSkyBlock) return
		val message = PartyChatParser.parse(event.text) ?: return
		if (message.author.equals(mc.user.name, ignoreCase = true)) return
		val shared = SharedMineshaftParser.parse(message.message) ?: return
		Debug.log { "SharedMineshaftWarp: ${message.author} shared ${shared.type.code}" }
		window.open(System.currentTimeMillis())
		notify(message.author, shared)
	}

	private fun onTick() {
		if (!config.enabled || !SkyBlock.isOnSkyBlock) return
		if (!KeyUtils.wasClicked(config.warpKey) || mc.screen != null) return
		if (!window.isOpen(System.currentTimeMillis(), config.windowSeconds)) return
		window.close()
		Debug.log { "SharedMineshaftWarp: sending !w" }
		PartyCommands.ignoreOwnWarp()
		mc.connection?.sendCommand("pc !w")
		MineshaftRole.onWarpRequested()
	}

	private fun notify(author: String, shared: SharedMineshaft) {
		val hint = if (config.warpKey == KeyUtils.NONE) {
			"Set a warp key in /tv → Mineshaft → Shared Mineshaft Warp"
		} else {
			"Press ${KeyUtils.keyName(config.warpKey)} to warp"
		}
		val name = "${shared.type.displayName} Mineshaft"
		if (config.showTitle) {
			mc.gui.setTimes(0, 60, 10)
			mc.gui.setSubtitle(Component.literal(hint).withStyle(ChatFormatting.YELLOW))
			mc.gui.setTitle(Component.literal(name).withStyle(shared.type.color))
		}
		val corpses = shared.corpses?.let { " ($it)" } ?: ""
		ChatUtils.send(
			Component.literal("$author found a ").withStyle(ChatFormatting.GRAY)
				.append(Component.literal(name).withStyle(shared.type.color))
				.append(Component.literal("$corpses. ").withStyle(ChatFormatting.GRAY))
				.append(Component.literal(hint).withStyle(ChatFormatting.YELLOW))
		)
		if (config.playSound) {
			mc.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_PLING, 1f))
		}
	}
}
