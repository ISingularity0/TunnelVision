package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class CrystalNotificationsConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Notify you about crystals waiting to be forged.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(
		name = "Available Title",
		desc = "Entering the Dwarven Mines while carrying a crystal shows \"<Crystal> available\" " +
			"with \"go to forge\" under it. Stays quiet when the forge has no open slot.",
	)
	@ConfigEditorBoolean
	var availableTitle = true

	@Expose
	@JvmField
	@ConfigOption(
		name = "Crystals Full Message",
		desc = "Send \"Crystals full\" to chat once when you carry every crystal and the forge is full.",
	)
	@ConfigEditorBoolean
	var fullMessage = true

	@Expose
	@JvmField
	@ConfigOption(
		name = "Widget",
		desc = "Show a HUD widget listing the crystals you are carrying. Move it with /tv hud.",
	)
	@ConfigEditorBoolean
	var widget = true

	@Expose
	@JvmField
	@ConfigOption(name = "Sound", desc = "Play a sound with these notifications.")
	@ConfigEditorBoolean
	var playSound = true
}
