package io.github.tunnelvisionmod.tunnelvision.utils

private val formattingCode = Regex("§.")

fun String.removeFormatting(): String = replace(formattingCode, "")
