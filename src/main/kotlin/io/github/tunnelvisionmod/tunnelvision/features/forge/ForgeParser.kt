package io.github.tunnelvisionmod.tunnelvision.features.forge

data class ForgeSlot(val slot: Int, val item: String, val isReady: Boolean)

object ForgeParser {
	private const val TAB_HEADER = "Forges:"
	private val slotLine = Regex("""^(\d+)\) (.+)$""")
	private val occupiedSlot = Regex("""^(.+): (.+)$""")
	private val readyStates = setOf("ready!", "ready")

	fun parseTab(lines: List<String>): List<ForgeSlot>? {
		val header = lines.indexOf(TAB_HEADER)
		if (header < 0) return null
		val slots = mutableListOf<ForgeSlot>()
		for (line in lines.drop(header + 1)) {
			val (slot, content) = slotLine.matchEntire(line)?.destructured ?: break
			val (item, state) = occupiedSlot.matchEntire(content)?.destructured ?: continue
			slots += ForgeSlot(slot.toInt(), item, state.lowercase() in readyStates)
		}
		return slots
	}
}
