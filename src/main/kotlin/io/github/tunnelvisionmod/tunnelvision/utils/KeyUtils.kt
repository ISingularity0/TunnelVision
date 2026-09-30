package io.github.tunnelvisionmod.tunnelvision.utils

import com.mojang.blaze3d.platform.InputConstants
import io.github.notenoughupdates.moulconfig.common.IMinecraft
import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import org.lwjgl.glfw.GLFW

object KeyUtils {
	const val NONE = -1

	private val heldKeys = mutableSetOf<Int>()

	fun isKeyDown(keyCode: Int): Boolean = when (keyCode) {
		NONE -> false
		in 0..GLFW.GLFW_MOUSE_BUTTON_LAST -> GLFW.glfwGetMouseButton(mc.window.handle(), keyCode) == GLFW.GLFW_PRESS
		else -> keyCode > 0 && InputConstants.isKeyDown(mc.window, keyCode)
	}

	fun wasClicked(keyCode: Int): Boolean {
		if (isKeyDown(keyCode)) return heldKeys.add(keyCode)
		heldKeys.remove(keyCode)
		return false
	}

	fun keyName(keyCode: Int): String = IMinecraft.INSTANCE.getKeyName(keyCode).text
}
