package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.config.LootMode
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftType
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MineshaftValueTest {
	@Test
	fun `maps mineshaft types to gemstones`() {
		assertEquals(GemstoneShaft.RUBY, GemstoneShaft.of(MineshaftType.RUBY_2))
		assertEquals(GemstoneShaft.RUBY, GemstoneShaft.of(MineshaftType.RUBY_CRYSTAL))
		assertEquals(GemstoneShaft.JADE, GemstoneShaft.of(MineshaftType.JADE_1))
		assertEquals(GemstoneShaft.JASPER, GemstoneShaft.of(MineshaftType.JASPER_CRYSTAL))
		assertEquals(GemstoneShaft.PERIDOT, GemstoneShaft.of(MineshaftType.PERIDOT_2))
		assertEquals(GemstoneShaft.CITRINE, GemstoneShaft.of(MineshaftType.CITRINE_CRYSTAL))
		assertEquals(GemstoneShaft.ONYX, GemstoneShaft.of(MineshaftType.ONYX_1))
		assertEquals(GemstoneShaft.AQUAMARINE, GemstoneShaft.of(MineshaftType.AQUAMARINE_2))
	}

	@Test
	fun `non gemstone mineshafts are not rated`() {
		assertNull(GemstoneShaft.of(MineshaftType.UMBER))
		assertNull(GemstoneShaft.of(MineshaftType.TITANIUM))
		assertNull(GemstoneShaft.of(MineshaftType.TUNGSTEN))
		assertNull(GemstoneShaft.of(MineshaftType.VANGUARD))
		assertNull(GemstoneShaft.of(MineshaftType.LITTLEFOOTS_DEN))
	}

	@Test
	fun `bazaar ids`() {
		assertEquals("FINE_RUBY_GEM", GemstoneShaft.RUBY.fineGemId)
		assertEquals("FINE_AMETHYST_GEM", GemstoneShaft.AMETHYST.fineGemId)
	}

	@Test
	fun `thresholds from the table`() {
		assertEquals(25_900, GemstoneShaft.RUBY.threshold(corpses = 2, crystalsFull = false))
		assertEquals(23_400, GemstoneShaft.RUBY.threshold(corpses = 4, crystalsFull = false))
		assertEquals(20_900, GemstoneShaft.RUBY.threshold(corpses = 3, crystalsFull = true))
		assertEquals(31_600, GemstoneShaft.OPAL.threshold(corpses = 3, crystalsFull = false))
		assertEquals(31_600, GemstoneShaft.AMBER.threshold(corpses = 3, crystalsFull = false))
		assertEquals(40_100, GemstoneShaft.TOPAZ.threshold(corpses = 4, crystalsFull = false))
		assertEquals(41_800, GemstoneShaft.JASPER.threshold(corpses = 3, crystalsFull = true))
		assertEquals(45_500, GemstoneShaft.PERIDOT.threshold(corpses = 4, crystalsFull = true))
		assertEquals(56_200, GemstoneShaft.CITRINE.threshold(corpses = 3, crystalsFull = false))
		assertEquals(50_300, GemstoneShaft.ONYX.threshold(corpses = 2, crystalsFull = true))
		assertEquals(53_400, GemstoneShaft.AQUAMARINE.threshold(corpses = 4, crystalsFull = false))
	}

	@Test
	fun `corpse count outside the table is clamped`() {
		assertEquals(25_900, GemstoneShaft.RUBY.threshold(corpses = 1, crystalsFull = false))
		assertEquals(23_400, GemstoneShaft.RUBY.threshold(corpses = 5, crystalsFull = false))
	}

	@Test
	fun `mine when price reaches the threshold`() {
		assertTrue(MineshaftValue.evaluate(MineshaftType.JASPER, corpses = 3, crystalsFull = false, price = 49_200.0)!!.shouldMine)
		assertFalse(MineshaftValue.evaluate(MineshaftType.JASPER, corpses = 3, crystalsFull = false, price = 49_199.0)!!.shouldMine)
	}

	@Test
	fun `verdict carries gemstone price and threshold`() {
		assertEquals(
			MineshaftVerdict(GemstoneShaft.TOPAZ, price = 27_692.0, threshold = 42_100, shouldMine = false),
			MineshaftValue.evaluate(MineshaftType.TOPAZ_1, corpses = 3, crystalsFull = false, price = 27_692.0),
		)
	}

	@Test
	fun `normal and greedy count all corpses`() {
		assertEquals(4, MineshaftValue.countedCorpses(mapOf("Lapis" to 2, "Umber" to 1, "Tungsten" to 1), LootMode.NORMAL))
		assertEquals(4, MineshaftValue.countedCorpses(mapOf("Lapis" to 2, "Umber" to 1, "Tungsten" to 1), LootMode.GREEDY))
	}

	@Test
	fun `lapis only counts lapis corpses`() {
		assertEquals(2, MineshaftValue.countedCorpses(mapOf("Lapis" to 2, "Umber" to 1, "Tungsten" to 1), LootMode.LAPIS_ONLY))
		assertEquals(0, MineshaftValue.countedCorpses(mapOf("Umber" to 2), LootMode.LAPIS_ONLY))
	}

	@Test
	fun `normal always uses the crystals full thresholds`() {
		assertTrue(MineshaftValue.usesFullThresholds(LootMode.NORMAL, crystalsFull = false))
		assertTrue(MineshaftValue.usesFullThresholds(LootMode.NORMAL, crystalsFull = true))
	}

	@Test
	fun `greedy and lapis only follow the crystals`() {
		for (mode in listOf(LootMode.GREEDY, LootMode.LAPIS_ONLY)) {
			assertFalse(MineshaftValue.usesFullThresholds(mode, crystalsFull = false))
			assertTrue(MineshaftValue.usesFullThresholds(mode, crystalsFull = true))
		}
	}

	@Test
	fun `fewer than two corpses means dont mine`() {
		val verdict = MineshaftValue.evaluate(MineshaftType.OPAL, corpses = 1, crystalsFull = false, price = 1_000_000.0)!!
		assertFalse(verdict.shouldMine)
		assertNull(verdict.threshold)
	}

	@Test
	fun `jasper is always worth mining below two corpses`() {
		val verdict = MineshaftValue.evaluate(MineshaftType.JASPER, corpses = 0, crystalsFull = false, price = 1.0)!!
		assertTrue(verdict.shouldMine)
		assertNull(verdict.threshold)
	}

	@Test
	fun `no verdict for other mineshafts`() {
		assertNull(MineshaftValue.evaluate(MineshaftType.UMBER, corpses = 3, crystalsFull = false, price = 1_000_000.0))
	}
}
