package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MiningStatsConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show a HUD widget with mining stats for your current island.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Sky Mall", desc = "Current Sky Mall buff, on every mining island. Open /hotm once if it is unknown.")
	@ConfigEditorBoolean
	var skyMall = true

	@Expose
	@JvmField
	@ConfigOption(name = "Mineshaft Mayhem", desc = "The buff you got from Mineshaft Mayhem, in Glacite Mineshafts.")
	@ConfigEditorBoolean
	var mayhem = true

	@Expose
	@JvmField
	@ConfigOption(name = "Mining Event", desc = "The Fortunate Freezing fortune bonus (Glacite Mineshafts), or the speed and fortune from Better Together (all mining islands).")
	@ConfigEditorBoolean
	var miningEvent = true

	@Expose
	@JvmField
	@ConfigOption(name = "Cold Resistance", desc = "Your Cold Resistance in Glacite Mineshafts. Requires Cold Resistance in the tab list Stats widget (/widget).")
	@ConfigEditorBoolean
	var coldResistance = true
}
