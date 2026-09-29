package io.github.tunnelvisionmod.tunnelvision.features.forge

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ForgeParserTest {
	private fun tab(vararg forgeLines: String) = listOf("Area: Dwarven Mines", "Forges:") + forgeLines + listOf("Commissions:", "Mithril Miner: 50%")

	@Test
	fun `parses running and ready slots`() {
		val slots = ForgeParser.parseTab(tab("1) Perfect Ruby Gemstone: 3h 2m", "2) Refined Diamond: Ready!"))
		assertEquals(
			listOf(ForgeSlot(1, "Perfect Ruby Gemstone", false), ForgeSlot(2, "Refined Diamond", true)),
			slots,
		)
	}

	@Test
	fun `accepts uppercase ready`() {
		assertEquals(listOf(ForgeSlot(1, "Refined Mithril", true)), ForgeParser.parseTab(tab("1) Refined Mithril: READY")))
	}

	@Test
	fun `skips empty and locked slots`() {
		val slots = ForgeParser.parseTab(tab("1) Refined Diamond: 12m 5s", "2) EMPTY", "3) LOCKED"))
		assertEquals(listOf(ForgeSlot(1, "Refined Diamond", false)), slots)
	}

	@Test
	fun `stops at next widget`() {
		val slots = ForgeParser.parseTab(tab("1) EMPTY"))
		assertEquals(emptyList<ForgeSlot>(), slots)
	}

	@Test
	fun `missing widget`() {
		assertNull(ForgeParser.parseTab(listOf("Area: Dwarven Mines", "Commissions:")))
	}
}
