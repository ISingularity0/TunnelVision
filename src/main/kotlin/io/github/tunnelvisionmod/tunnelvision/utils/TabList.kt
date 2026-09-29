package io.github.tunnelvisionmod.tunnelvision.utils

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.mixin.PlayerTabOverlayAccessor

object TabList {
	var lines: List<String> = emptyList()
		private set

	fun register() {
		EventBus.on<ClientTickEvent> { update() }
	}

	private fun update() {
		if (!SkyBlock.isOnSkyBlock || mc.player == null) {
			lines = emptyList()
			return
		}
		lines = (mc.gui.tabList as PlayerTabOverlayAccessor).`tunnelvision$getPlayerInfos`().mapNotNull { info ->
			info.tabListDisplayName?.string?.removeFormatting()?.trim()?.takeIf { it.isNotEmpty() }
		}
	}
}
