package io.github.tunnelvisionmod.tunnelvision.utils

private val formattingCode = Regex("§.")

fun String.removeFormatting(): String = replace(formattingCode, "")

fun formatDuration(millis: Long): String {
	val totalSeconds = millis / 1000
	val hours = totalSeconds / 3600
	val minutes = totalSeconds % 3600 / 60
	val seconds = totalSeconds % 60
	return when {
		hours > 0 -> "${hours}h ${minutes}m"
		minutes > 0 -> "${minutes}m ${seconds}s"
		else -> "${seconds}s"
	}
}
