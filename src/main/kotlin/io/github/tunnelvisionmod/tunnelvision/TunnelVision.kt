package io.github.tunnelvisionmod.tunnelvision

import net.fabricmc.api.ClientModInitializer
import org.slf4j.LoggerFactory

object TunnelVision : ClientModInitializer {
	const val MOD_ID = "tunnelvision"
	val logger = LoggerFactory.getLogger(MOD_ID)

	override fun onInitializeClient() {
		logger.info("TunnelVision initialized")
	}
}
