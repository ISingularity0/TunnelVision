package io.github.tunnelvisionmod.tunnelvision.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack

fun ItemStack.loreLines(): List<String> =
	get(DataComponents.LORE)?.lines()?.map { it.string.removeFormatting().trim() } ?: emptyList()
