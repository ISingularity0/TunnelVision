package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.Config
import io.github.notenoughupdates.moulconfig.annotations.Category
import io.github.notenoughupdates.moulconfig.common.text.StructuredText

class TunnelVisionConfig : Config() {
	override fun getTitle(): StructuredText = StructuredText.of("TunnelVision")

	@Expose
	@JvmField
	@Category(name = "General", desc = "General settings")
	var general = GeneralConfig()
}
