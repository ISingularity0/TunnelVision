package io.github.tunnelvisionmod.tunnelvision.features.mining

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.DisconnectEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.utils.ChatUtils
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.Sidebar
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock
import io.github.tunnelvisionmod.tunnelvision.utils.TabList
import net.minecraft.network.chat.Component

/**
 * Tracks which Glacite Mineshaft you are in and what it still holds.
 *
 * The type comes off the scoreboard and is announced as soon as it is known. The corpse count
 * comes from a tab list widget that Hypixel fills in seconds later, so the entry message
 * deliberately does not wait for it - [corpseCount] is kept live for features that want it.
 */
object MineshaftDetection {
	private const val MINESHAFT_ISLAND = "mineshaft"

	private val config get() = ConfigManager.config.mineshaft.mineshaftType

	/** The current mineshaft, or null when not in one (or not identified yet). */
	var type: MineshaftType? = null
		private set

	/** Corpses listed in the tab widget right now, or null while the widget is absent. */
	var corpseCount: Int? = null
		private set

	private var announced = false
	private var lastIsland: String? = null

	fun init() {
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<LocationChangedEvent> { onLocationChanged(it) }
		EventBus.on<DisconnectEvent> {
			lastIsland = null
			reset()
		}
	}

	// Only an actual island change starts a new mineshaft; Hypixel may repeat the location packet.
	private fun onLocationChanged(event: LocationChangedEvent) {
		if (event.island == lastIsland) return
		lastIsland = event.island
		reset()
	}

	private fun onTick() {
		if (SkyBlock.island != MINESHAFT_ISLAND) return

		val count = MineshaftParser.parseCorpseCount(TabList.lines)
		if (count != corpseCount) Debug.log { "Mineshaft: corpses -> ${count ?: "widget absent"}" }
		corpseCount = count

		if (announced) return
		val detected = MineshaftParser.parseType(Sidebar.lines) ?: return
		type = detected
		announced = true
		announce(detected)
	}

	private fun announce(mineshaft: MineshaftType) {
		Debug.log { "Mineshaft: entered ${mineshaft.code}" }
		if (!config.announceEntry) return
		ChatUtils.send(Component.literal(mineshaft.displayName).withStyle(mineshaft.color))
		if (config.showTitle) {
			mc.gui.setTimes(0, 50, 10)
			mc.gui.setTitle(Component.literal(mineshaft.displayName).withStyle(mineshaft.color))
		}
	}

	private fun reset() {
		type = null
		corpseCount = null
		announced = false
	}
}
