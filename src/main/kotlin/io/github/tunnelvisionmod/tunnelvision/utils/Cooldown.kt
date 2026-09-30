package io.github.tunnelvisionmod.tunnelvision.utils

class Cooldown(private val durationMs: Long) {
	private var lastUse: Long? = null

	fun tryUse(now: Long): Boolean {
		val last = lastUse
		if (last != null && now - last < durationMs) return false
		lastUse = now
		return true
	}
}
