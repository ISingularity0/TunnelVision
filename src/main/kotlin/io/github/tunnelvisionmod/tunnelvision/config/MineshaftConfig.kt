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
	@ConfigOption(name = "Cut Loose Tracker", desc = "Track your kills for the Cut Loose perk from the Fossil Essence Shop.")
	@Accordion
	var cutLooseTracker = CutLooseTrackerConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Mineshaft Type", desc = "Type and corpse count when you enter a Glacite Mineshaft.")
	@Accordion
	var mineshaftType = MineshaftTypeConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Party Share", desc = "Tell your party which mineshaft you entered and what corpses it has.")
	@Accordion
	var partyShare = MineshaftPartyShareConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Shared Mineshaft Warp", desc = "Warp into a mineshaft a party member shared with one key press.")
	@Accordion
	var sharedMineshaftWarp = SharedMineshaftWarpConfig()
}
