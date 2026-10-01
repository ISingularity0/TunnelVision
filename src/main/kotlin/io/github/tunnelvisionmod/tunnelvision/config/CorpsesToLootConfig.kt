package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class CorpsesToLootConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show a HUD widget with the corpses worth looting and the fossil. §bRequires Mineshaft Waypoints → Fossil for the fossil. §7Uses §eLapis Only §7and §ePrice §7from Mineshaft Value.")
	@ConfigEditorBoolean
	var enabled = false
}
