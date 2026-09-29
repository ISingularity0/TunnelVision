package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class CutLooseTrackerConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show a HUD widget counting your mob kills in the current Glacite Mineshaft (Cut Loose stacks up to 10).")
	@ConfigEditorBoolean
	var enabled = false
}
