package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class GeneralConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Debug Mode", desc = "Log extra information for development.")
	@ConfigEditorBoolean
	var debugMode = false
}
