package io.github.tunnelvisionmod.tunnelvision.events

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents

object EventHooks {
	fun register() {
		ClientTickEvents.END_CLIENT_TICK.register { ClientTickEvent.post() }
		ClientReceiveMessageEvents.ALLOW_GAME.register { message, overlay ->
			overlay || !ChatReceivedEvent(message).post().isCancelled
		}
	}
}
