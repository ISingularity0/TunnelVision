package io.github.tunnelvisionmod.tunnelvision.config

import com.google.gson.annotations.Expose
import io.github.notenoughupdates.moulconfig.annotations.ConfigEditorBoolean
import io.github.notenoughupdates.moulconfig.annotations.ConfigOption

class LanternReminderConfig {
	@Expose
	@JvmField
	@ConfigOption(name = "Enabled", desc = "Remind you to place your lantern (Will-o'-wisp, Glacite Lantern, ...) in Glacite Mineshafts and warn when it expires.")
	@ConfigEditorBoolean
	var enabled = false

	@Expose
	@JvmField
	@ConfigOption(name = "Entry Reminder", desc = "When you enter a mineshaft with a lantern in your inventory, show \"Place your ...!\" under the mineshaft type.")
	@ConfigEditorBoolean
	var entryReminder = true

	@Expose
	@JvmField
	@ConfigOption(name = "Expired Alert", desc = "Show a title when your lantern despawns.")
	@ConfigEditorBoolean
	var expiredAlert = true

	@Expose
	@JvmField
	@ConfigOption(name = "Sound", desc = "Play a sound when your lantern despawns.")
	@ConfigEditorBoolean
	var playSound = true
}
