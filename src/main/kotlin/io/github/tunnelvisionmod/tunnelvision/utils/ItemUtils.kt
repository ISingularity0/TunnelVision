package io.github.tunnelvisionmod.tunnelvision.utils

import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack

fun ItemStack.loreLines(): List<String> =
	get(DataComponents.LORE)?.lines()?.map { it.string.removeFormatting().trim() } ?: emptyList()

fun ItemStack.customData(): CompoundTag? = get(DataComponents.CUSTOM_DATA)?.copyTag()

fun ItemStack.skyblockId(): String? = customData()?.getString("id")?.orElse(null)

fun ItemStack.plainName(): String = hoverName.string.removeFormatting().trim()
