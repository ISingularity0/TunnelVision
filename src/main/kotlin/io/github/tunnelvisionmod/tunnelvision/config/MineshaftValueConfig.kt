package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MineshaftValueConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "When you enter a gemstone mineshaft, show whether you should mine its gemstones, based on the Fine gemstone price and the number of corpses.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Price", desc = "Which Bazaar price of the Fine gemstone to compare: what you get with a sell offer, or when you sell instantly.")
	@ConfigEditorDropdown
	var priceType = BazaarPriceType.SELL_OFFER

	@Expose
	@JvmField
	@ConfigOption(name = "Lapis Only", desc = "Only count Lapis corpses, for when you skip the corpses that need a key. Also used by Corpses to Loot. With fewer than 2 counted corpses it always says DON'T MINE, except for Jasper.")
	@ConfigEditorBoolean
	var lapisOnly = false

	@Expose
	@JvmField
	@ConfigOption(name = "Chat Message", desc = "Also send the verdict with price and threshold to chat.")
	@ConfigEditorBoolean
	var sendChat = true
}

enum class BazaarPriceType(private val label: String) {
	SELL_OFFER("Sell Offer"),
	INSTANT_SELL("Instant Sell");

	override fun toString() = label
}
