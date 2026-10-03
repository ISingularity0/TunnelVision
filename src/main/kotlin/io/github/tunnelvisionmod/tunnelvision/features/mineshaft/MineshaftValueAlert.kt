package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.compat.Compat
import io.github.tunnelvisionmod.tunnelvision.config.BazaarPriceType
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.features.crystals.CrystalNotifications
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftDetection
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftParser
import io.github.tunnelvisionmod.tunnelvision.utils.Bazaar
import io.github.tunnelvisionmod.tunnelvision.utils.ChatUtils
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.TabList
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object MineshaftValueAlert {
	private val config get() = ConfigManager.config.mineshaft.mineshaftValue

	private var done = false

	fun init() {
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<LocationChangedEvent> { done = false }
	}

	private fun onTick() {
		if (!config.enabled || !SkyBlock.isOnMiningIsland) return
		Bazaar.refreshIfStale()
		if (done || !SkyBlock.isInMineshaft) return
		val type = MineshaftDetection.type ?: return
		if (GemstoneShaft.of(type) == null) {
			done = true
			return
		}
		val verdict = currentVerdict() ?: return
		done = true
		Debug.log { "MineshaftValue: ${type.code}, loot mode: ${config.lootMode}, crystals full: ${CrystalNotifications.crystalsAndForgeFull}, $verdict" }
		announce(verdict)
	}

	fun currentVerdict(): MineshaftVerdict? {
		if (!SkyBlock.isInMineshaft) return null
		val type = MineshaftDetection.type ?: return null
		val gemstone = GemstoneShaft.of(type) ?: return null
		val corpses = MineshaftParser.parseCorpses(TabList.lines)?.let { MineshaftValue.countedCorpses(it, config.lootMode) } ?: return null
		val prices = Bazaar.price(gemstone.fineGemId) ?: return null
		val price = when (config.priceType) {
			BazaarPriceType.SELL_OFFER -> prices.sellOffer
			BazaarPriceType.INSTANT_SELL -> prices.instantSell
		}
		val fullThresholds = MineshaftValue.usesFullThresholds(config.lootMode, CrystalNotifications.crystalsAndForgeFull)
		return MineshaftValue.evaluate(type, corpses, fullThresholds, price)
	}

	private fun announce(verdict: MineshaftVerdict) {
		val headline = if (verdict.shouldMine) {
			Component.literal("MINE").withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD)
		} else {
			Component.literal("DON'T MINE").withStyle(ChatFormatting.RED, ChatFormatting.BOLD)
		}
		val price = "Fine ${verdict.gemstone.gemName} ${"%,.0f".format(verdict.price)}"
		val reason = when {
			verdict.threshold != null -> "$price / ${"%,d".format(verdict.threshold)} needed"
			verdict.shouldMine -> "$price · ${verdict.gemstone.gemName} is always worth mining"
			else -> "$price · fewer than ${GemstoneShaft.MIN_CORPSES} corpses"
		}
		val details = Component.literal(reason).withStyle(ChatFormatting.GRAY)
		Compat.setTitleTimes(0, 60, 10)
		Compat.setSubtitle(details)
		Compat.setTitle(headline)
		if (config.sendChat) ChatUtils.send(headline.copy().append(Component.literal(" ")).append(details))
	}
}
