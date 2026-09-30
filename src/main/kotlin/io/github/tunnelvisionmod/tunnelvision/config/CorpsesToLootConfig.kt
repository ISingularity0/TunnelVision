package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class CorpsesToLootConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show which corpses are worth looting and the fossil (needs Mineshaft Waypoints with Fossil on). Uses \"Lapis Only\" and the price setting from Mineshaft Value.")
	@ConfigEditorBoolean
	var enabled = false
}
