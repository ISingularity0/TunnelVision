package io.github.tunnelvisionmod.tunnelvision.features.mining

object MineshaftParser {
	private const val CORPSE_HEADER = "Frozen Corpses:"
	private val corpseLine = Regex("""^\w+: (?:NOT )?LOOTED$""")

	/**
	 * Reads the mineshaft variant off the scoreboard. The server id is the last word of the date
	 * line at the bottom of the sidebar and carries the variant as a tag, e.g. `09/29/25 mTOPA_1x`.
	 */
	fun parseType(sidebarLines: List<String>): MineshaftType? = sidebarLines.firstNotNullOfOrNull { line ->
		val serverId = line.substringAfterLast(' ')
		MineshaftType.entries.firstOrNull { it.code in serverId }
	}

	/**
	 * Counts the corpses listed in the `Frozen Corpses` tab list widget, or null when the widget is
	 * missing — the player can turn it off in Hypixel's settings, and it takes a moment to populate
	 * after entering a mineshaft.
	 */
	fun parseCorpseCount(tabLines: List<String>): Int? {
		val header = tabLines.indexOf(CORPSE_HEADER)
		if (header < 0) return null
		return tabLines.asSequence().drop(header + 1).takeWhile { corpseLine.matches(it) }.count()
	}
}
