package io.github.tunnelvisionmod.tunnelvision

import io.github.tunnelvisionmod.tunnelvision.commands.TunnelVisionCommand
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.EventHooks
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.Minecraft
import org.slf4j.LoggerFactory

object TunnelVision : ClientModInitializer {
	const val MOD_ID = "tunnelvision"
	val logger = LoggerFactory.getLogger(MOD_ID)
	val mc: Minecraft get() = Minecraft.getInstance()

	override fun onInitializeClient() {
		ConfigManager.load()
		EventHooks.register()
		TunnelVisionCommand.register()
		logger.info("TunnelVision initialized")
	}
}
