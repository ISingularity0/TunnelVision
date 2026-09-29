package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.Accordion
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorButton
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption
import io.github.tunnelvisionmod.tunnelvision.hud.HudManager

class GeneralConfig {
	@JvmField
	@ConfigOption(name = "HUD Editor", desc = "Move and resize all TunnelVision HUD widgets. Also available via /tv hud.")
	@ConfigEditorButton(buttonText = "Open")
	val openHudEditor = Runnable { HudManager.openEditor() }

	@Expose
	@JvmField
	@ConfigOption(name = "Pickaxe Ability", desc = "Notifications and timer for your pickaxe ability cooldown.")
	@Accordion
	var pickaxeAbility = PickaxeAbilityConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Forge Notification", desc = "Get notified in the Dwarven Mines when something in your Forge is done.")
	@Accordion
	var forgeNotification = ForgeNotificationConfig()

	@Expose
	@JvmField
	@ConfigOption(name = "Mining Effects", desc = "Show the remaining time of Cold Resistance IV and Filet O' Fortune while mining.")
	@Accordion
	var miningEffects = MiningEffectsConfig()
}
