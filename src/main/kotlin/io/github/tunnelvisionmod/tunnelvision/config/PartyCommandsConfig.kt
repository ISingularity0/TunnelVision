package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class PartyCommandsConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "React to commands in party chat. They only work if you are the party leader.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Party Transfer", desc = "Transfer the party to whoever types !pt or !ptme.")
	@ConfigEditorBoolean
	var transfer = true

	@Expose
	@JvmField
	@ConfigOption(name = "Party Warp", desc = "Warp the party when someone (including you) types !w or !warp. At most once every 5 seconds.")
	@ConfigEditorBoolean
	var warp = true
}
