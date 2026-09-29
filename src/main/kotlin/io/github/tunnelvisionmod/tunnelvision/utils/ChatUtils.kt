package io.github.tunnelvisionmod.tunnelvision.utils

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object ChatUtils {
	private val prefix: Component
		get() = Component.literal("[TunnelVision] ").withStyle(ChatFormatting.AQUA)

	fun send(message: Component) {
		mc.player?.sendSystemMessage(Component.empty().append(prefix).append(message))
	}
}
