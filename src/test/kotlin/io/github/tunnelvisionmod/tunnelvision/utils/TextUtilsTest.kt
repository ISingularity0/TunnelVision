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
}
