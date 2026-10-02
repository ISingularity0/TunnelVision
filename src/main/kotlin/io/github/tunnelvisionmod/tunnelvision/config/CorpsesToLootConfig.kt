package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class CorpsesToLootConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show a Mineshaft To-Dos widget: corpses worth looting, the fossil and the crystal of a crystal shaft. Corpses without a key don't count. §bRequires Mineshaft Waypoints → Fossil for the fossil and /hotm opened once for the crystal. §7Uses §eLoot Mode §7and §ePrice §7from Mineshaft Value.")
	@ConfigEditorBoolean
	var enabled = false
}
