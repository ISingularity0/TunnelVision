package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.RightClickEvent
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.customData
import io.github.tunnelvisionmod.tunnelvision.utils.plainName
import io.github.tunnelvisionmod.tunnelvision.utils.skyblockId
import net.minecraft.ChatFormatting
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.EntityHitResult

object BlueCheeseCorpseLock {
	private const val UPGRADE_MODULE_KEY = "drill_part_upgrade_module"
	private const val WARNING_COOLDOWN_MS = 1000L

	private val config get() = ConfigManager.config.mineshaft.blueCheeseCorpseLock

	private var lastWarning = 0L

	fun init() {
		EventBus.on<RightClickEvent> { onRightClick(it) }
	}

	private fun onRightClick(event: RightClickEvent) {
		if (!config.enabled || !SkyBlock.isInMineshaft) return
		val player = mc.player ?: return
		val corpse = targetedCorpse() ?: return
		if (player.mainHandItem.hasBlueCheese()) return
		if (!player.inventory.hasBlueCheeseDrill()) return
		event.cancel()
		warn(corpse)
	}

	private fun targetedCorpse(): CorpseType? {
		val stand = (mc.hitResult as? EntityHitResult)?.entity as? ArmorStand ?: return null
		if (stand.isInvisible) return null
		val helmet = stand.getItemBySlot(EquipmentSlot.HEAD)
		if (helmet.isEmpty) return null
		return CorpseType.fromHelmet(helmet.skyblockId(), helmet.plainName())
	}

	private fun ItemStack.hasBlueCheese(): Boolean =
		!isEmpty && BlueCheese.isUpgradeModule(customData()?.getString(UPGRADE_MODULE_KEY)?.orElse(null))

	private fun Inventory.hasBlueCheeseDrill(): Boolean =
		(0 until Inventory.INVENTORY_SIZE).any { getItem(it).hasBlueCheese() }

	private fun warn(corpse: CorpseType) {
		val now = System.currentTimeMillis()
		if (now - lastWarning < WARNING_COOLDOWN_MS) return
		lastWarning = now
		Debug.log { "BlueCheeseCorpseLock: blocked looting $corpse corpse while not holding blue cheese drill" }
		if (config.showTitle) {
			mc.gui.setTimes(0, 20, 5)
			mc.gui.setTitle(Component.literal("Blue Cheese!").withStyle(ChatFormatting.RED))
		}
		if (config.playSound) {
			mc.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.VILLAGER_NO, 1f))
		}
	}
}
