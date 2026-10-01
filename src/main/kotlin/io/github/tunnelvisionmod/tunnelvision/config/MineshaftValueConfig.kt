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
	@ConfigOption(name = "Loot Mode", desc = "§eLapis Only§7: only Lapis corpses. §eNormal§7: all corpses in a shaft worth mining, otherwise Lapis + Vanguard. §eGreedy§7: all corpses until crystals and forge are full, then like Normal.")
	@ConfigEditorDropdown
	var lootMode = LootMode.LAPIS_ONLY

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

enum class LootMode(private val label: String) {
	LAPIS_ONLY("Lapis Only"),
	NORMAL("Normal"),
	GREEDY("Greedy");

	override fun toString() = label
}
