package io.github.tunnelvisionmod.tunnelvision.features.party

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ChatReceivedEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.utils.Cooldown
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock

object PartyCommands {
	private const val WARP_COOLDOWN_MS = 5_000L

	private val config get() = ConfigManager.config.general.partyCommands
	private val warpCooldown = Cooldown(WARP_COOLDOWN_MS)

	fun init() {
		EventBus.on<ChatReceivedEvent> { onChat(it) }
	}

	private fun onChat(event: ChatReceivedEvent) {
		if (!config.enabled || !SkyBlock.isOnSkyBlock) return
		val message = PartyChatParser.parse(event.text) ?: return
		val command = PartyCommand.parse(message.message) ?: return
		if (!command.isEnabled() || !command.canBeUsedBy(message.author, mc.user.name)) return
		if (command == PartyCommand.WARP && !warpCooldown.tryUse(System.currentTimeMillis())) return
		val hypixelCommand = command.hypixelCommand(message.author)
		Debug.log { "PartyCommands: ${message.author} used ${message.message} -> /$hypixelCommand" }
		mc.connection?.sendCommand(hypixelCommand)
	}

	private fun PartyCommand.isEnabled(): Boolean = when (this) {
		PartyCommand.TRANSFER -> config.transfer
		PartyCommand.WARP -> config.warp
	}
}
