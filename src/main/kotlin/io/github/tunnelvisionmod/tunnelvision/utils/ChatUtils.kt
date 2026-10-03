package io.github.tunnelvisionmod.tunnelvision.utils

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.compat.Compat
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object ChatUtils {
	private val prefix: Component = Component.literal("[TunnelVision] ").withStyle(ChatFormatting.DARK_AQUA)

	fun send(message: Component) {
		Compat.chat.addClientSystemMessage(Component.empty().append(prefix).append(message))
	}
}
