package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

enum class LootRule(private val label: String, private val types: Set<CorpseType>) {
	ALL("all corpses", CorpseType.entries.toSet()),
	LAPIS("Lapis only", setOf(CorpseType.LAPIS)),
	LAPIS_AND_VANGUARD("Lapis + Vanguard", setOf(CorpseType.LAPIS, CorpseType.VANGUARD));

	fun includes(type: CorpseType) = type in types

	fun label(vanguardShaft: Boolean): String = if (this == LAPIS_AND_VANGUARD && !vanguardShaft) LAPIS.label else label
}

object CorpseLoot {
	private const val TAB_HEADER = "Frozen Corpses:"
	private val corpseLine = Regex("""^(\w+): (NOT )?LOOTED$""")

	fun rule(lapisOnly: Boolean, crystalsFull: Boolean, shouldMine: Boolean?): LootRule = when {
		lapisOnly -> LootRule.LAPIS
		!crystalsFull || shouldMine == true -> LootRule.ALL
		else -> LootRule.LAPIS_AND_VANGUARD
	}

	fun parseUnlooted(tabLines: List<String>): Map<CorpseType, Int>? {
		val header = tabLines.indexOf(TAB_HEADER)
		if (header < 0) return null
		return tabLines.asSequence()
			.drop(header + 1)
			.map { corpseLine.matchEntire(it) }
			.takeWhile { it != null }
			.filter { it!!.groupValues[2].isNotEmpty() }
			.mapNotNull { CorpseType.fromTabName(it!!.groupValues[1]) }
			.groupingBy { it }
			.eachCount()
	}

	fun toLoot(unlooted: Map<CorpseType, Int>, rule: LootRule): Map<CorpseType, Int> = unlooted.filterKeys { rule.includes(it) }
}
