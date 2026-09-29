package io.github.tunnelvisionmod.tunnelvision.hud

import net.minecraft.network.chat.Component

abstract class HudWidget(val id: String, val displayName: String, val defaultPosition: HudPosition) {
	abstract val isEnabled: Boolean

	abstract fun getLines(): List<Component>

	abstract fun getExampleLines(): List<Component>
}
