package com.cube.lib.parser

import android.text.Editable
import android.text.Html
import org.xml.sax.XMLReader

/**
 * Parser class for handling <li> and <ul> tags
 */
class ListTagParser : Html.TagHandler
{
	var first = true
	var parent: String? = null
	var index = 1

	override fun handleTag(opening: Boolean, tag: String?, output: Editable?, xmlReader: XMLReader?)
	{
		if (tag.equals("ul"))
		{
			parent = "ul"
			index = 1
		}
		else if (tag.equals("ol"))
		{
			parent = "ol"
			index = 1
		}

		if (tag.equals("li"))
		{
			var lastChar: Char = 0.toChar()
			if (output?.length ?: 0 > 0)
			{
				lastChar = output?.get(output.length - 1) ?: lastChar
			}

			if (parent.equals("ul"))
			{
				if (first)
				{
					if (lastChar == '\n')
					{
						output?.append("\t•  ")
					}
					else
					{
						output?.append("\n\t•  ")
					}

					first = false
				}
				else
				{
					first = true
				}
			}
			else
			{
				if (first)
				{
					if (lastChar == '\n')
					{
						output?.append("\t" + index + ". ")
					}
					else
					{
						output?.append("\n\t" + index + ". ")
					}

					first = false
					index++
				}
				else
				{
					first = true
				}
			}
		}
	}
}
