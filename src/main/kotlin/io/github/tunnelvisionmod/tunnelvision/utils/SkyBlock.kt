package io.github.tunnelvisionmod.tunnelvision.utils

import io.github.tunnelvisionmod.tunnelvision.TunnelVision.mc
import io.github.tunnelvisionmod.tunnelvision.events.DisconnectEvent
import io.github.tunnelvisionmod.tunnelvision.events.EventBus
import io.github.tunnelvisionmod.tunnelvision.events.LocationChangedEvent
import io.github.tunnelvisionmod.tunnelvision.events.post
import net.hypixel.data.type.GameType
import net.hypixel.modapi.HypixelModAPI
import net.hypixel.modapi.packet.impl.clientbound.event.ClientboundLocationPacket

object SkyBlock {
	private const val MINESHAFT_ISLAND = "mineshaft"

	var isOnSkyBlock = false
		private set
	var island: String? = null
		private set

	val isInMineshaft: Boolean get() = isOnSkyBlock && island == MINESHAFT_ISLAND

	fun register() {
		val api = HypixelModAPI.getInstance()
		api.subscribeToEventPacket(ClientboundLocationPacket::class.java)
		api.createHandler(ClientboundLocationPacket::class.java) { packet ->
			mc.execute {
				val onSkyBlock = packet.serverType.orElse(null) == GameType.SKYBLOCK
				update(onSkyBlock, if (onSkyBlock) packet.mode.orElse(null) else null)
			}
		}
		EventBus.on<DisconnectEvent> { update(false, null) }
	}

	private fun update(onSkyBlock: Boolean, newIsland: String?) {
		isOnSkyBlock = onSkyBlock
		island = newIsland
		Debug.log { "Location: onSkyBlock=$onSkyBlock island=$newIsland" }
		LocationChangedEvent(onSkyBlock, newIsland).post()
	}
}
