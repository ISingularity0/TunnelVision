package io.github.tunnelvisionmod.tunnelvision.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TextUtilsTest {
	@Test
	fun `removes formatting codes`() {
		assertEquals("Mining Speed Boost: 42s", "§e§lMining Speed Boost§r§7: §a42s".removeFormatting())
	}

	@Test
	fun `keeps plain text`() {
		assertEquals("Pickaxe Ability:", "Pickaxe Ability:".removeFormatting())
	}

	@Test
	fun `formats durations`() {
		assertEquals("45s", formatDuration(45_000))
		assertEquals("12m 30s", formatDuration(750_000))
		assertEquals("1h 5m", formatDuration(3_900_000))
		assertEquals("0s", formatDuration(0))
	}
}
