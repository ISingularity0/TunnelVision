package io.github.tunnelvisionmod.tunnelvision.features.mining

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class CooldownSyncTest {
	@Test
	fun `starts countdown from tab when idle`() {
		assertEquals(42 * 20, CooldownSync.resync(ticksLeft = 0, tabSeconds = 42))
	}

	@Test
	fun `ignores short tab cooldown when idle`() {
		assertNull(CooldownSync.resync(ticksLeft = 0, tabSeconds = 1))
	}

	@Test
	fun `never counts up when tab lags behind`() {
		assertNull(CooldownSync.resync(ticksLeft = 40 * 20, tabSeconds = 42))
		assertNull(CooldownSync.resync(ticksLeft = 10 * 20, tabSeconds = 60))
	}

	@Test
	fun `ignores tab rounding`() {
		assertNull(CooldownSync.resync(ticksLeft = 42 * 20 + 15, tabSeconds = 42))
	}

	@Test
	fun `corrects down when timer is too slow`() {
		assertEquals(30 * 20, CooldownSync.resync(ticksLeft = 45 * 20, tabSeconds = 30))
	}
}
