package io.github.tunnelvisionmod.tunnelvision.utils

import io.github.tunnelvisionmod.tunnelvision.TunnelVision
import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component

/**
 * Writes the tab list exactly as the mod sees it to a file, for working out what a widget really
 * looks like instead of guessing at its wording.
 */
object TabDump {
	private var lastMenu: String? = null

	fun register() {
		EventBus.on<ClientTickEvent> { snapshotOpenMenu() }
	}

	/**
	 * Remembers the open container menu. A command cannot be typed while one is open, so the dump
	 * has to be taken continuously and written out afterwards.
	 */
	private fun snapshotOpenMenu() {
		val screen = mc.screen as? AbstractContainerScreen<*> ?: return
		val title = screen.title.string.removeFormatting()
		lastMenu = buildString {
			appendLine("title=" + title)
			appendLine("slots=" + screen.menu.slots.size)
			for ((i, slot) in screen.menu.slots.withIndex()) {
				val item = slot.item
				if (item.isEmpty) continue
				appendLine("[" + i + "] " + item.plainName())
				for (line in item.loreLines()) appendLine("      | " + line)
			}
		}
	}

	fun dump() {
		val file = FabricLoader.getInstance().configDir.resolve("${TunnelVision.MOD_ID}/tabdump.txt").toFile()
		val text = buildString {
			appendLine("island=${SkyBlock.island} onSkyBlock=${SkyBlock.isOnSkyBlock}")
			appendLine("--- ${TabList.lines.size} tab lines ---")
			TabList.lines.forEachIndexed { i, line -> appendLine("[$i] $line") }
			appendLine("--- ${TabList.footerLines.size} footer lines ---")
			TabList.footerLines.forEachIndexed { i, line -> appendLine("[$i] $line") }
		}
		runCatching {
			file.parentFile?.mkdirs()
			file.writeText(text)
		}.onFailure {
			TunnelVision.logger.error("Failed to write tab dump", it)
			ChatUtils.send(Component.literal("Failed to write tab dump, see the log.").withStyle(ChatFormatting.RED))
			return
		}
		TunnelVision.logger.info("Tab dump written to ${file.absolutePath}\n$text")
		ChatUtils.send(
			Component.literal("Wrote ${TabList.lines.size} tab lines to ").withStyle(ChatFormatting.GREEN)
				.append(Component.literal("config/tunnelvision/tabdump.txt").withStyle(ChatFormatting.YELLOW)),
		)
	}

	/** Writes the most recently opened container menu, with every item's lore, to a file. */
	fun dumpGui() {
		val text = lastMenu
		if (text == null) {
			ChatUtils.send(Component.literal("Open the menu once, close it, then run this.").withStyle(ChatFormatting.RED))
			return
		}
		val file = FabricLoader.getInstance().configDir.resolve("${TunnelVision.MOD_ID}/guidump.txt").toFile()
		runCatching {
			file.parentFile?.mkdirs()
			file.writeText(text)
		}.onFailure {
			TunnelVision.logger.error("Failed to write gui dump", it)
			ChatUtils.send(Component.literal("Failed to write gui dump, see the log.").withStyle(ChatFormatting.RED))
			return
		}
		TunnelVision.logger.info("Gui dump written to " + file.absolutePath)
		TunnelVision.logger.info(text)
		ChatUtils.send(
			Component.literal("Wrote the last menu to ").withStyle(ChatFormatting.GREEN)
				.append(Component.literal("config/tunnelvision/guidump.txt").withStyle(ChatFormatting.YELLOW)),
		)
	}
}
