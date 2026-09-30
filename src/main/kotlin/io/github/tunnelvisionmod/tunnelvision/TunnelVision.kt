package io.github.tunnelvisionmod.tunnelvision

import io.github.tunnelvisionmod.tunnelvision.commands.TunnelVisionCommand
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.EventHooks
import io.github.tunnelvisionmod.tunnelvision.features.crystals.CrystalNotifications
import io.github.tunnelvisionmod.tunnelvision.features.effects.MiningEffects
import io.github.tunnelvisionmod.tunnelvision.features.forge.ForgeNotification
import io.github.tunnelvisionmod.tunnelvision.features.mineshaft.BlueCheeseCorpseLock
import io.github.tunnelvisionmod.tunnelvision.features.mineshaft.CorpsesToLoot
import io.github.tunnelvisionmod.tunnelvision.features.mineshaft.CutLooseTracker
import io.github.tunnelvisionmod.tunnelvision.features.mineshaft.LanternReminder
import io.github.tunnelvisionmod.tunnelvision.features.mineshaft.MineshaftValueAlert
import io.github.tunnelvisionmod.tunnelvision.features.mineshaft.MineshaftWaypoints
import io.github.tunnelvisionmod.tunnelvision.features.mineshaft.SharedMineshaftWarp
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftDetection
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftPartyShare
import io.github.tunnelvisionmod.tunnelvision.features.mining.PickaxeAbility
import io.github.tunnelvisionmod.tunnelvision.features.party.PartyCommands
import io.github.tunnelvisionmod.tunnelvision.features.pristine.HidePristineMessages
import io.github.tunnelvisionmod.tunnelvision.features.pristine.WrongGearWarning
import io.github.tunnelvisionmod.tunnelvision.features.stats.MiningStats
import io.github.tunnelvisionmod.tunnelvision.hud.HudManager
import io.github.tunnelvisionmod.tunnelvision.utils.Sidebar
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.Storage
import io.github.tunnelvisionmod.tunnelvision.utils.TabList
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.Minecraft
import org.slf4j.LoggerFactory

object TunnelVision : ClientModInitializer {
	const val MOD_ID = "tunnelvision"
	val logger = LoggerFactory.getLogger(MOD_ID)
	val mc: Minecraft get() = Minecraft.getInstance()

	override fun onInitializeClient() {
		ConfigManager.load()
		HudManager.load()
		Storage.load()
		EventHooks.register()
		SkyBlock.register()
		TabList.register()
		Sidebar.register()
		PickaxeAbility.init()
		MineshaftDetection.init()
		ForgeNotification.init()
		CrystalNotifications.init()
		MiningEffects.init()
		BlueCheeseCorpseLock.init()
		CutLooseTracker.init()
		MineshaftWaypoints.init()
		MineshaftPartyShare.init()
		WrongGearWarning.init()
		HidePristineMessages.init()
		PartyCommands.init()
		SharedMineshaftWarp.init()
		MiningStats.init()
		LanternReminder.init()
		MineshaftValueAlert.init()
		CorpsesToLoot.init()
		TunnelVisionCommand.register()
		logger.info("TunnelVision initialized")
	}
}
