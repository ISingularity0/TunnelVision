package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.config.ConfigManager
import io.github.tunnelvisionmod.tunnelvision.events.ClientTickEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.features.mining.MineshaftDetection
import io.github.tunnelvisionmod.tunnelvision.utils.Bazaar
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.LocationTracker
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock

object GemstoneRoutes {
	private val config get() = ConfigManager.config.mineshaft.gemstoneRoutes

	private var running: RouteChoice? = null
	private val location = LocationTracker()

	fun init() {
		EventBus.on<ClientTickEvent> { onTick() }
		EventBus.on<LocationChangedEvent> { onLocationChanged(it) }
	}

	private fun onLocationChanged(event: LocationChangedEvent) {
		if (!location.isNewLocation(event)) return
		if (running != null) RouteRunner.stop()
		running = null
	}

	private fun onTick() {
		if (!config.enabled || !SkyBlock.isInMineshaft) return
		Bazaar.refreshIfStale()
		val type = MineshaftDetection.type ?: return
		if (running == null && !(CorpsesToLoot.todosDone && MineshaftValueAlert.currentVerdict()?.shouldMine == true)) return
		val choice = RouteChoice.of(type.code, MineshaftRole.role, RouteLibrary::exists) ?: return
		if (choice == running) return
		val route = RouteLibrary.load(choice.name) ?: return
		running = choice
		RouteRunner.start(route.part(choice.part)) { config.enabled && SkyBlock.isInMineshaft }
		Debug.log { "GemstoneRoutes: ${choice.name} ${choice.part} for ${type.code}" }
	}
}
