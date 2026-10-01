package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.config.LootMode
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftType

enum class GemstoneShaft(
	private val codePrefix: String,
	val gemName: String,
	private val crystalsAvailable: List<Int>,
	private val crystalsFull: List<Int>,
) {
	RUBY("RUBY", "Ruby", listOf(25_900, 24_600, 23_400), listOf(22_000, 20_900, 19_900)),
	OPAL("OPAL", "Opal", BP7_AVAILABLE, BP7_FULL),
	AMETHYST("AMET", "Amethyst", BP7_AVAILABLE, BP7_FULL),
	SAPPHIRE("SAPP", "Sapphire", BP7_AVAILABLE, BP7_FULL),
	JADE("JADE", "Jade", BP7_AVAILABLE, BP7_FULL),
	AMBER("AMBE", "Amber", BP7_AVAILABLE, BP7_FULL),
	TOPAZ("TOPA", "Topaz", listOf(44_400, 42_100, 40_100), listOf(37_700, 35_800, 34_100)),
	JASPER("JASP", "Jasper", listOf(51_800, 49_200, 46_700), listOf(44_000, 41_800, 39_800)),
	PERIDOT("PERI", "Peridot", BP9_AVAILABLE, BP9_FULL),
	CITRINE("CITR", "Citrine", BP9_AVAILABLE, BP9_FULL),
	ONYX("ONYX", "Onyx", BP9_AVAILABLE, BP9_FULL),
	AQUAMARINE("AQUA", "Aquamarine", BP9_AVAILABLE, BP9_FULL);

	val fineGemId: String get() = "FINE_${gemName.uppercase()}_GEM"

	fun threshold(corpses: Int, crystalsFull: Boolean): Int {
		val values = if (crystalsFull) this.crystalsFull else crystalsAvailable
		return values[corpses.coerceIn(MIN_CORPSES, MIN_CORPSES + values.size - 1) - MIN_CORPSES]
	}

	companion object {
		const val MIN_CORPSES = 2

		fun of(type: MineshaftType): GemstoneShaft? = entries.firstOrNull { type.code.startsWith(it.codePrefix) }
	}
}

private val BP7_AVAILABLE = listOf(33_300, 31_600, 30_000)
private val BP7_FULL = listOf(28_300, 26_900, 25_600)
private val BP9_AVAILABLE = listOf(59_200, 56_200, 53_400)
private val BP9_FULL = listOf(50_300, 47_800, 45_500)

data class MineshaftVerdict(val gemstone: GemstoneShaft, val price: Double, val threshold: Int?, val shouldMine: Boolean)

object MineshaftValue {
	private const val LAPIS = "Lapis"

	fun countedCorpses(corpses: Map<String, Int>, mode: LootMode): Int =
		if (mode == LootMode.LAPIS_ONLY) corpses[LAPIS] ?: 0 else corpses.values.sum()

	fun usesFullThresholds(mode: LootMode, crystalsFull: Boolean): Boolean = mode == LootMode.NORMAL || crystalsFull

	fun evaluate(type: MineshaftType, corpses: Int, crystalsFull: Boolean, price: Double): MineshaftVerdict? {
		val gemstone = GemstoneShaft.of(type) ?: return null
		if (corpses < GemstoneShaft.MIN_CORPSES) return MineshaftVerdict(gemstone, price, threshold = null, shouldMine = gemstone == GemstoneShaft.JASPER)
		val threshold = gemstone.threshold(corpses, crystalsFull)
		return MineshaftVerdict(gemstone, price, threshold, price >= threshold)
	}
}
