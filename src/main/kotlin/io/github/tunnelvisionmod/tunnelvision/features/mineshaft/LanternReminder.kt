package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ChatReceivedEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.MineshaftEnteredEvent
import io.github.tunnelvisionmod.tunnelvision.utils.ChatUtils
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.plainName
import net.minecraft.ChatFormatting
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.entity.player.Inventory

object LanternReminder {
	private val config get() = ConfigManager.config.mineshaft.lanternReminder

	fun init() {
		EventBus.on<MineshaftEnteredEvent> { onMineshaftEntered() }
		EventBus.on<ChatReceivedEvent> { onChat(it) }
	}

	private fun onMineshaftEntered() {
		if (!config.enabled || !config.entryReminder) return
		val inventory = mc.player?.inventory ?: return
		val lantern = Lantern.strongest((0 until Inventory.INVENTORY_SIZE).map { inventory.getItem(it).plainName() }) ?: return
		Debug.log { "LanternReminder: reminding to place $lantern" }
		val text = Component.literal("Place your ${lantern.itemName}!").withStyle(ChatFormatting.YELLOW)
		ChatUtils.send(text)
		val typeConfig = ConfigManager.config.mineshaft.mineshaftType
		if (typeConfig.announceEntry && typeConfig.showTitle) mc.gui.setSubtitle(text)
	}

	private fun onChat(event: ChatReceivedEvent) {
		if (!config.enabled || !config.expiredAlert || !SkyBlock.isOnMiningIsland) return
		val lantern = Lantern.fromDespawnMessage(event.text) ?: return
		Debug.log { "LanternReminder: $lantern despawned" }
		val text = Component.literal("${lantern.itemName} expired!").withStyle(ChatFormatting.YELLOW)
		mc.gui.setTimes(0, 40, 10)
		mc.gui.setTitle(text)
		if (config.playSound) mc.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.NOTE_BLOCK_PLING, 1f))
	}
}
