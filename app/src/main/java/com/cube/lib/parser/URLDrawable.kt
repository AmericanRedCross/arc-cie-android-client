package com.cube.lib.parser

import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable

class URLDrawable() : BitmapDrawable()
{
	// the drawable that you need to set, you could set the initial drawing
	// with the loading image if you need to
	public var drawable: Drawable? = null

	override fun draw(canvas: Canvas)
	{
		drawable?.draw(canvas)
	}
}
