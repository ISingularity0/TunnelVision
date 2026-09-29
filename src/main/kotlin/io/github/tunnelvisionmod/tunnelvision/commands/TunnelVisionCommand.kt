package io.github.tunnelvisionmod.tunnelvision.commands

import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback

object TunnelVisionCommand {
	fun register() {
		ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
			for (name in listOf("tunnelvision", "tv")) {
				dispatcher.register(literal(name).executes {
					ConfigManager.openScreen()
					1
				})
			}
		}
	}
}
