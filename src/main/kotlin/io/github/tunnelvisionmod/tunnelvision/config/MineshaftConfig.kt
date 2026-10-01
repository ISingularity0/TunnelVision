package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MineshaftConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Blue Cheese Corpse Lock", desc = "Only allow looting corpses with your Blue Cheese drill.")
	@Accordion
	var blueCheeseCorpseLock = BlueCheeseCorpseLockConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Cut Loose Tracker", desc = "Track your kills for the Cut Loose perk.")
	@Accordion
	var cutLooseTracker = CutLooseTrackerConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Mineshaft Type", desc = "Show the mineshaft type and corpses when you enter.")
	@Accordion
	var mineshaftType = MineshaftTypeConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Party Share", desc = "Share the mineshaft type and corpses with your party.")
	@Accordion
	var partyShare = MineshaftPartyShareConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Shared Mineshaft Warp", desc = "Warp into a shared mineshaft with one key press.")
	@Accordion
	var sharedMineshaftWarp = SharedMineshaftWarpConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Mineshaft Waypoints", desc = "Waypoints for corpses and the fossil.")
	@Accordion
	var mineshaftWaypoints = MineshaftWaypointsConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Lantern Reminder", desc = "Remind you to place your lantern and warn when it expires.")
	@Accordion
	var lanternReminder = LanternReminderConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Mineshaft Value", desc = "Tell you if the gemstones are worth mining.")
	@Accordion
	var mineshaftValue = MineshaftValueConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Mineshaft To-Do", desc = "Widget with the corpses worth looting and the fossil.")
	@Accordion
	var corpsesToLoot = CorpsesToLootConfig()
}
