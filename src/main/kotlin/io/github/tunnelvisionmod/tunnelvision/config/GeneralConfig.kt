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
}
