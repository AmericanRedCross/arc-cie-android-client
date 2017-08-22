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
