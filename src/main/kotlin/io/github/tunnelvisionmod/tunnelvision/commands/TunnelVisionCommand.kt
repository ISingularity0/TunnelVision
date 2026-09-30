package io.github.tunnelvisionmod.tunnelvision.commands

import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.features.crystals.CrystalNotifications
import io.github.tunnelvisionmod.tunnelvision.features.crystals.CrystalType
import io.github.tunnelvisionmod.tunnelvision.hud.HudManager
import io.github.tunnelvisionmod.tunnelvision.utils.ChatUtils
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.ClientCommands.literal
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

object TunnelVisionCommand {
	fun register() {
		ClientCommandRegistrationCallback.EVENT.register { dispatcher, _ ->
			for (name in listOf("tunnelvision", "tv")) {
				dispatcher.register(
					literal(name)
						.executes {
							ConfigManager.openScreen()
							1
						}
						.then(literal("hud").executes {
							HudManager.openEditor()
							1
						})
						.then(crystalCommand())
				)
			}
		}
	}

	/**
	 * Crystals are rare enough that waiting to find one is no way to test the notifications, so
	 * this pretends you carry them. `/tv crystal all`, `/tv crystal none`, `/tv crystal <name>` to
	 * toggle one, and `/tv crystal entry` to replay the entry notification.
	 */
	private fun crystalCommand() = literal("crystal")
		.executes {
			showCarried()
			1
		}
		.then(literal("all").executes {
			CrystalNotifications.setCarriedForTesting(CrystalType.entries.associateWith { true })
			showCarried()
			1
		})
		.then(literal("none").executes {
			CrystalNotifications.setCarriedForTesting(CrystalType.entries.associateWith { false })
			showCarried()
			1
		})
		.then(literal("entry").executes {
			CrystalNotifications.replayEntryNotification()
			ChatUtils.send(Component.literal("Replaying the entry notification.").withStyle(ChatFormatting.GREEN))
			1
		})
		.also { node ->
			for (crystal in CrystalType.entries) {
				node.then(literal(crystal.displayName.lowercase()).executes {
					val carried = crystal !in CrystalNotifications.carriedCrystals
					CrystalNotifications.setCarriedForTesting(mapOf(crystal to carried))
					showCarried()
					1
				})
			}
		}

	private fun showCarried() {
		val carried = CrystalNotifications.carriedCrystals
		val text = Component.literal("Crystals (" + carried.size + "/" + CrystalType.entries.size + "): ")
			.withStyle(ChatFormatting.AQUA)
		if (carried.isEmpty()) {
			text.append(Component.literal("none").withStyle(ChatFormatting.GRAY))
		} else {
			for ((index, crystal) in carried.withIndex()) {
				if (index > 0) text.append(Component.literal(", ").withStyle(ChatFormatting.GRAY))
				text.append(Component.literal(crystal.displayName).withStyle(crystal.color))
			}
		}
		ChatUtils.send(text)
	}
}
