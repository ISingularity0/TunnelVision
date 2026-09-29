package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

enum class CorpseType(private val helmetId: String, private val helmetName: String) {
	LAPIS("LAPIS_ARMOR_HELMET", "Lapis Armor Helmet"),
	TUNGSTEN("MINERAL_HELMET", "Mineral Helmet"),
	UMBER("ARMOR_OF_YOG_HELMET", "Yog Helmet"),
	VANGUARD("VANGUARD_HELMET", "Vanguard Helmet");

	companion object {
		fun fromHelmet(skyblockId: String?, name: String): CorpseType? =
			entries.firstOrNull { it.helmetId == skyblockId || it.helmetName == name }
	}
}
