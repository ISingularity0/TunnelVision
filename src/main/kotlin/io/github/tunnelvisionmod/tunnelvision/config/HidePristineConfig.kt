package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class HidePristineConfig {
	@Expose
	@JvmField
	@ConfigOption(
		name = "Enabled",
		desc = "Hide the \"PRISTINE! You found ...\" lines from chat. Wrong Gear still warns about low procs, " +
			"so you only lose the spam you do not need while your gear is right.",
	)
	@ConfigEditorBoolean
	var enabled = false
}
