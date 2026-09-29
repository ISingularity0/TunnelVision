package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MineshaftPartyShareConfig {
	@Expose
	@JvmField
	@ConfigOption(
		name = "Enabled",
		desc = "Send the mineshaft type and its corpses to your party chat once per mineshaft, " +
			"e.g. \"Mineshafttype: JASP_1, Corpses: Lapis 2, Tungsten 1\". This posts publicly to your party.",
	)
	@ConfigEditorBoolean
	var enabled = false
}
