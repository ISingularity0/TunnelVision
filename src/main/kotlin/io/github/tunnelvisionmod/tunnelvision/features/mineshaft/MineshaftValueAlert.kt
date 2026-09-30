package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.BazaarPriceType
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
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
		val gemstone = GemstoneShaft.of(type)
		if (gemstone == null) {
			done = true
			return
		}
		val corpses = MineshaftParser.parseCorpses(TabList.lines)?.let { MineshaftValue.countedCorpses(it, config.lapisOnly) } ?: return
		val prices = Bazaar.price(gemstone.fineGemId) ?: return
		val price = when (config.priceType) {
			BazaarPriceType.SELL_OFFER -> prices.sellOffer
			BazaarPriceType.INSTANT_SELL -> prices.instantSell
		}
		val verdict = MineshaftValue.evaluate(type, corpses, crystalsFull = false, price = price) ?: return
		done = true
		Debug.log { "MineshaftValue: ${type.code}, $corpses counted corpses${if (config.lapisOnly) " (lapis only)" else ""}, $verdict" }
		announce(verdict)
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
		mc.gui.setTimes(0, 60, 10)
		mc.gui.setSubtitle(details)
		mc.gui.setTitle(headline)
		if (config.sendChat) ChatUtils.send(headline.copy().append(Component.literal(" ")).append(details))
	}
}
