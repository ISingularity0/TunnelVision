package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorSlider
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class WrongGearConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Warn whenever a Pristine proc drops fewer gemstones than your gear should give, so you notice you are mining with the wrong setup.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Low Proc", desc = "Warn when a Pristine proc gives this many gemstones or fewer. Set this just under what your correct gear normally drops.")
	@ConfigEditorSlider(minValue = 1f, maxValue = 25f, minStep = 1f)
	var lowProc = 2

	@Expose
	@JvmField
	@ConfigOption(name = "Warning Title", desc = "Show a red \"Wrong Gear!\" title with the warning.")
	@ConfigEditorBoolean
	var showTitle = true

	@Expose
	@JvmField
	@ConfigOption(name = "Warning Sound", desc = "Play a sound with the warning.")
	@ConfigEditorBoolean
	var playSound = true
}
