package io.github.tunnelvisionmod.tunnelvision.hud

import io.github.notenoughupdates.moulconfig.managed.ManagedDataFile
import io.github.tunnelvisionmod.tunnelvision.TunnelVision
import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.compat.Compat
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import kotlin.math.roundToInt

data class HudBounds(val x: Int, val y: Int, val width: Int, val height: Int) {
	fun contains(mouseX: Double, mouseY: Double) = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height
}

object HudManager {
	const val MIN_SCALE = 0.5f
	const val MAX_SCALE = 3f
	private const val LINE_SPACING = 1

	private val widgets = mutableListOf<HudWidget>()
	private lateinit var data: ManagedDataFile<HudData>

	val enabledWidgets: List<HudWidget> get() = widgets.filter { it.isEnabled }

	fun load() {
		val file = FabricLoader.getInstance().configDir.resolve("${TunnelVision.MOD_ID}/hud.json").toFile()
		data = ManagedDataFile.create(file, HudData::class.java) {
			loadFailed = { _, e -> TunnelVision.logger.error("Failed to load HUD positions", e) }
			saveFailed = { _, e -> TunnelVision.logger.error("Failed to save HUD positions", e) }
		}
		HudElementRegistry.addLast(Identifier.fromNamespaceAndPath(TunnelVision.MOD_ID, "hud")) { graphics, _ -> render(graphics) }
	}

	fun register(widget: HudWidget) {
		widgets += widget
	}

	fun positionOf(widget: HudWidget): HudPosition =
		data.instance.positions.getOrPut(widget.id) { widget.defaultPosition.copy() }

	fun reset(widget: HudWidget) {
		data.instance.positions[widget.id] = widget.defaultPosition.copy()
	}

	fun save() = data.saveToFile()

	fun openEditor() {
		mc.schedule { Compat.setScreen(HudEditorScreen()) }
	}

	fun bounds(widget: HudWidget, lines: List<Component>, screenWidth: Int, screenHeight: Int): HudBounds {
		val position = positionOf(widget)
		val contentWidth = lines.maxOfOrNull { mc.font.width(it) } ?: 0
		val contentHeight = lines.size * (mc.font.lineHeight + LINE_SPACING) - LINE_SPACING
		val width = (contentWidth * position.scale).roundToInt()
		val height = (contentHeight * position.scale).roundToInt()
		val x = (position.x.coerceIn(0f, 1f) * (screenWidth - width).coerceAtLeast(0)).roundToInt()
		val y = (position.y.coerceIn(0f, 1f) * (screenHeight - height).coerceAtLeast(0)).roundToInt()
		return HudBounds(x, y, width, height)
	}

	fun draw(graphics: GuiGraphicsExtractor, widget: HudWidget, lines: List<Component>) {
		if (lines.isEmpty()) return
		val bounds = bounds(widget, lines, graphics.guiWidth(), graphics.guiHeight())
		val scale = positionOf(widget).scale
		val pose = graphics.pose()
		pose.pushMatrix()
		pose.translate(bounds.x.toFloat(), bounds.y.toFloat())
		pose.scale(scale, scale)
		lines.forEachIndexed { index, line ->
			graphics.text(mc.font, line, 0, index * (mc.font.lineHeight + LINE_SPACING), -1, true)
		}
		pose.popMatrix()
	}

	private fun render(graphics: GuiGraphicsExtractor) {
		if (!SkyBlock.isOnSkyBlock || Compat.screen is HudEditorScreen) return
		enabledWidgets.forEach { draw(graphics, it, it.getLines()) }
	}
}
