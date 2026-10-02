package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParser

data class RouteWaypoint(val pos: Pos, val blocks: List<Pos>, val path: List<Pos> = emptyList()) {
	val centre: Pos by lazy {
		val x = blocks.sumOf { it.x }.toDouble() / blocks.size
		val y = blocks.sumOf { it.y }.toDouble() / blocks.size
		val z = blocks.sumOf { it.z }.toDouble() / blocks.size
		blocks.minBy { (it.x - x) * (it.x - x) + (it.y - y) * (it.y - y) + (it.z - z) * (it.z - z) }
	}
}

data class GemstoneRoute(val layout: String, val split: Int?, val waypoints: List<RouteWaypoint>) {
	enum class Part { ALL, FIRST, SECOND }

	fun part(part: Part): List<RouteWaypoint> = when {
		split == null || part == Part.ALL -> waypoints
		part == Part.FIRST -> waypoints.take(split)
		else -> waypoints.drop(split).let { tail ->
			tail.indices.reversed().map { i -> tail[i].copy(path = tail.getOrNull(i + 1)?.path?.reversed() ?: emptyList()) }
		}
	}

	companion object {
		fun parse(json: String): GemstoneRoute {
			val root = JsonParser.parseString(json).asJsonObject
			return GemstoneRoute(
				layout = root.get("layout").asString,
				split = root.get("split")?.takeUnless { it.isJsonNull }?.asInt,
				waypoints = root.getAsJsonArray("waypoints").map { it.asJsonObject.toWaypoint() },
			)
		}

		private fun JsonObject.toWaypoint() = RouteWaypoint(
			Pos(get("x").asInt, get("y").asInt, get("z").asInt),
			getAsJsonArray("blocks").map { it.asJsonArray.toPos() },
			getAsJsonArray("path")?.map { it.asJsonArray.toPos() } ?: emptyList(),
		)

		private fun JsonArray.toPos() = Pos(this[0].asInt, this[1].asInt, this[2].asInt)
	}
}
