package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class GemstoneRoutesConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show a route through the best gemstone veins once your to-dos are done and the mineshaft is worth mining. Jasper splits the route when someone is warped in. §7Uses §eMineshaft Value §7and §eMineshaft To-Do§7.")
	@ConfigEditorBoolean
	var enabled = false
}
