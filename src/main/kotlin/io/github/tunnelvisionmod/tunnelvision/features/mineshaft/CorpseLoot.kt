package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.config.LootMode

enum class LootRule(private val types: Set<CorpseType>) {
	ALL(CorpseType.entries.toSet()),
	LAPIS(setOf(CorpseType.LAPIS)),
	LAPIS_AND_VANGUARD(setOf(CorpseType.LAPIS, CorpseType.VANGUARD));

	fun includes(type: CorpseType) = type in types
}

object CorpseLoot {
	private const val TAB_HEADER = "Frozen Corpses:"
	private val corpseLine = Regex("""^(\w+): (NOT )?LOOTED$""")

	fun rule(mode: LootMode, crystalsFull: Boolean, shouldMine: Boolean?): LootRule = when {
		mode == LootMode.LAPIS_ONLY -> LootRule.LAPIS
		mode == LootMode.GREEDY && !crystalsFull -> LootRule.ALL
		shouldMine == true -> LootRule.ALL
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
