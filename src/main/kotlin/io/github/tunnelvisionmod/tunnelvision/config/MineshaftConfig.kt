package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MineshaftConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Entry Message", desc = "Announce the mineshaft type in chat when you enter a Glacite Mineshaft.")
	@ConfigEditorBoolean
	var announceEntry = true

	@Expose
	@JvmField
	@ConfigOption(name = "Entry Title", desc = "Also show the mineshaft type as a title on screen.")
	@ConfigEditorBoolean
	var showTitle = false
}
