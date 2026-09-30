package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorKeybind
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import io.github.tunnelvisionmod.tunnelvision.utils.KeyUtils

class SharedMineshaftWarpConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "When a party member shares a mineshaft in party chat, let you warp in with one key press (sends !w to party chat).")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Warp Key", desc = "Key that sends !w to party chat after a mineshaft was shared.")
	@ConfigEditorKeybind(defaultKey = KeyUtils.NONE)
	var warpKey = KeyUtils.NONE

	@Expose
	@JvmField
	@ConfigOption(name = "Time Window", desc = "How many seconds after the shared message the key works. 0 = no time limit.")
	@ConfigEditorSlider(minValue = 0f, maxValue = 20f, minStep = 1f)
	var windowSeconds = 10

	@Expose
	@JvmField
	@ConfigOption(name = "Title", desc = "Show a title with the mineshaft type and the key to press.")
	@ConfigEditorBoolean
	var showTitle = true

	@Expose
	@JvmField
	@ConfigOption(name = "Sound", desc = "Play a sound when a mineshaft is shared.")
	@ConfigEditorBoolean
	var playSound = true
}
