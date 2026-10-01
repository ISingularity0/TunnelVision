package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MineshaftTypeConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Entry Message", desc = "Show the mineshaft type in chat when you enter.")
	@ConfigEditorBoolean
	var announceEntry = false

	@Expose
	@JvmField
	@ConfigOption(name = "Entry Title", desc = "Show the mineshaft type as a title.")
	@ConfigEditorBoolean
	var showTitle = false
}
