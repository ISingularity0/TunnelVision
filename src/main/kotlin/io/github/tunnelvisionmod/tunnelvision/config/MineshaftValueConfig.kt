package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorDropdown
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class MineshaftValueConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Show if the gemstones are worth mining when you enter.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Price", desc = "Which Bazaar price to use for Fine gemstones.")
	@ConfigEditorDropdown
	var priceType = BazaarPriceType.SELL_OFFER

	@Expose
	@JvmField
	@ConfigOption(name = "Lapis Only", desc = "Only count Lapis corpses.")
	@ConfigEditorBoolean
	var lapisOnly = false

	@Expose
	@JvmField
	@ConfigOption(name = "Chat Message", desc = "Also send the result with price to chat.")
	@ConfigEditorBoolean
	var sendChat = true
}

enum class BazaarPriceType(private val label: String) {
	SELL_OFFER("Sell Offer"),
	INSTANT_SELL("Instant Sell");

	override fun toString() = label
}
