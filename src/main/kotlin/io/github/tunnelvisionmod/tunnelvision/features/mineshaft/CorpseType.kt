package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

enum class CorpseType(private val helmetId: String, private val helmetName: String, val tabName: String, val keyName: String?) {
	LAPIS("LAPIS_ARMOR_HELMET", "Lapis Armor Helmet", "Lapis", null),
	TUNGSTEN("MINERAL_HELMET", "Mineral Helmet", "Tungsten", "Tungsten Key"),
	UMBER("ARMOR_OF_YOG_HELMET", "Yog Helmet", "Umber", "Umber Key"),
	VANGUARD("VANGUARD_HELMET", "Vanguard Helmet", "Vanguard", "Skeleton Key");

	companion object {
		fun fromHelmet(skyblockId: String?, name: String): CorpseType? =
			entries.firstOrNull { it.helmetId == skyblockId || it.helmetName == name }

		fun fromTabName(name: String): CorpseType? = entries.firstOrNull { it.tabName == name }
	}
}
