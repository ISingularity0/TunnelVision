package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class PickaxeAbilityConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Ready Title", desc = "Show a big title on screen when your pickaxe ability is ready again.")
	@ConfigEditorBoolean
	var showTitle = true

	@Expose
	@JvmField
	@ConfigOption(name = "Ready Sound", desc = "Play a sound when your pickaxe ability is ready again.")
	@ConfigEditorBoolean
	var playSound = true

	@Expose
	@JvmField
	@ConfigOption(name = "Cooldown Timer", desc = "Show a HUD widget with the remaining cooldown.")
	@ConfigEditorBoolean
	var showWidget = true
}
