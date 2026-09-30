package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MineshaftWaypointsConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show waypoints for possible corpse spots, found corpses and the fossil of your current Glacite Mineshaft.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Corpse Spots", desc = "Mark every known spot a corpse can spawn at. A spot disappears once you have seen it; if a corpse is there, it becomes a colored corpse waypoint.")
	@ConfigEditorBoolean
	var corpseSpots = true

	@Expose
	@JvmField
	@ConfigOption(name = "Fossil", desc = "Mark the fossil of the mineshaft until you mine one of its blocks.")
	@ConfigEditorBoolean
	var fossil = true

	@Expose
	@JvmField
	@ConfigOption(name = "Labels", desc = "Show a name and distance above each waypoint.")
	@ConfigEditorBoolean
	var labels = true
}
