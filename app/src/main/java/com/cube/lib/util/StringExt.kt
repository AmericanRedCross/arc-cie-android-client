package com.cube.lib.util

/**
 * Multiplies a string by the given amount
 */
operator fun String.times(amount: Int): String
{
	var newStr = ""
	repeat (amount)
	{
		newStr += this
	}

	return newStr
}

/**
 * Escapes certain characters for use in a CSV export
 */
fun String.escapeCsv(): String
{
	return if (isEmpty()) "" else "\"" + replace("\"", "\"\"") + "\""
}
