package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftDetection
import io.github.tunnelvisionmod.tunnelvision.utils.Bazaar
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock

object GemstoneRoutes {
	private const val MINESHAFT_ISLAND = "mineshaft"
	private const val WARP_IN_WINDOW_MS = 60_000L

	private val config get() = ConfigManager.config.mineshaft.gemstoneRoutes

	private var warpRequestedAt = 0L
	private var role = WarpRole.SOLO
	private var running: RouteChoice? = null
	private var lastIsland: String? = null

	fun init() {
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<LocationChangedEvent> { onLocationChanged(it) }
	}

	fun onWarpRequested() {
		warpRequestedAt = System.currentTimeMillis()
	}

	fun onPartyWarped() {
		if (SkyBlock.isInMineshaft && role == WarpRole.SOLO) {
			role = WarpRole.WARPED_SOMEONE
			Debug.log { "GemstoneRoutes: warped someone in" }
		}
	}

	private fun onLocationChanged(event: LocationChangedEvent) {
		if (event.island == lastIsland) return
		lastIsland = event.island
		if (running != null) RouteRunner.stop()
		running = null
		role = if (event.island == MINESHAFT_ISLAND && System.currentTimeMillis() - warpRequestedAt < WARP_IN_WINDOW_MS) WarpRole.WARPED_IN else WarpRole.SOLO
		if (role == WarpRole.WARPED_IN) Debug.log { "GemstoneRoutes: warped into a shared mineshaft" }
	}

	private fun onTick() {
		if (!config.enabled || !SkyBlock.isInMineshaft) return
		Bazaar.refreshIfStale()
		val type = MineshaftDetection.type ?: return
		if (running == null && !(CorpsesToLoot.todosDone && MineshaftValueAlert.currentVerdict()?.shouldMine == true)) return
		val choice = RouteChoice.of(type.code, role, RouteLibrary::exists) ?: return
		if (choice == running) return
		val route = RouteLibrary.load(choice.name) ?: return
		running = choice
		RouteRunner.start(route.part(choice.part)) { config.enabled && SkyBlock.isInMineshaft }
		Debug.log { "GemstoneRoutes: ${choice.name} ${choice.part} for ${type.code}" }
	}
}
