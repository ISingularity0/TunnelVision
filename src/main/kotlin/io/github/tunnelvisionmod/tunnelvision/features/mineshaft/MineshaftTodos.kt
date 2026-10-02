package io.github.tunnelvisionmod.tunnelvision.features.mineshaft

import io.github.tunnelvisionmod.tunnelvision.features.crystals.CrystalType

sealed interface Todo {
	data class Corpse(val type: CorpseType, val count: Int, val hasKey: Boolean) : Todo
	data object Fossil : Todo
	data class GrabCrystal(val crystal: CrystalType) : Todo
}

object MineshaftTodos {
	fun list(toLoot: Map<CorpseType, Int>, carriedItems: Set<String>, fossilPending: Boolean, crystalToGrab: CrystalType?): List<Todo> {
		val todos = mutableListOf<Todo>()
		todos += toLoot.entries.sortedBy { it.key.ordinal }.map { (type, count) ->
			Todo.Corpse(type, count, hasKey = type.keyName == null || type.keyName in carriedItems)
		}
		if (fossilPending) todos += Todo.Fossil
		crystalToGrab?.let { todos += Todo.GrabCrystal(it) }
		return todos
	}

	fun isDone(todos: List<Todo>): Boolean = todos.all { it is Todo.Corpse && !it.hasKey }

	fun crystalToGrab(crystalShaft: CrystalType?, crystalsKnown: Boolean, carried: Set<CrystalType>): CrystalType? =
		crystalShaft?.takeIf { crystalsKnown && it !in carried }
}
