package io.github.tunnelvisionmod.tunnelvision.features.mining

object CooldownSync {
	const val TICKS_PER_SECOND = 20
	private const val CORRECTION_THRESHOLD_TICKS = 30
	private const val MIN_START_SECONDS = 5
	private const val NATURAL_END_TICKS = 3 * TICKS_PER_SECOND

	fun resync(ticksLeft: Int, tabSeconds: Int): Int? {
		val tabTicks = tabSeconds * TICKS_PER_SECOND
		if (ticksLeft == 0) return tabTicks.takeIf { tabSeconds >= MIN_START_SECONDS }
		return tabTicks.takeIf { it < ticksLeft - CORRECTION_THRESHOLD_TICKS }
	}

	fun onAvailable(ticksLeft: Int): Int = if (ticksLeft in 1..NATURAL_END_TICKS) 1 else 0
}
