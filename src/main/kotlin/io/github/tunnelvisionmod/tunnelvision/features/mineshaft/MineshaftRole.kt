package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.events.DisconnectEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.utils.Debug
import io.github.tunnelvisionmod.tunnelvision.utils.LocationTracker
import io.github.tunnelvisionmod.tunnelvision.utils.SkyBlock

/**
 * Which side of a shared mineshaft you are on: you warped someone in, someone warped you in, or
 * you are mining alone.
 *
 * The warp key is pressed before the teleport lands, so a warp counts for [WARP_IN_WINDOW_MS]
 * and arriving in a mineshaft within that window reads as [WarpRole.WARPED_IN].
 */
object MineshaftRole {
	private const val MINESHAFT_ISLAND = "mineshaft"
	private const val WARP_IN_WINDOW_MS = 60_000L

	private val location = LocationTracker()
	private var warpRequestedAt = 0L

	var role = WarpRole.SOLO
		private set

	/** The fossil and the crystal belong to whoever opened the mineshaft, not to the warped-in player. */
	val isWarpedIn: Boolean get() = role == WarpRole.WARPED_IN

	fun init() {
		EventBus.on<LocationChangedEvent> { onLocationChanged(it) }
		EventBus.on<DisconnectEvent> {
			location.forget()
			warpRequestedAt = 0L
			role = WarpRole.SOLO
		}
	}

	/** You asked a party member to warp you into the mineshaft they shared. */
	fun onWarpRequested() {
		warpRequestedAt = System.currentTimeMillis()
	}

	/** A party member asked you to warp them into your mineshaft. */
	fun onPartyWarped() {
		if (SkyBlock.isInMineshaft && role == WarpRole.SOLO) {
			role = WarpRole.WARPED_SOMEONE
			Debug.log { "MineshaftRole: warped someone in" }
		}
	}

	private fun onLocationChanged(event: LocationChangedEvent) {
		if (!location.isNewLocation(event)) return
		val warpedIn = event.island == MINESHAFT_ISLAND && System.currentTimeMillis() - warpRequestedAt < WARP_IN_WINDOW_MS
		role = if (warpedIn) WarpRole.WARPED_IN else WarpRole.SOLO
		Debug.log { "MineshaftRole: $role on ${event.server}" }
	}
}
