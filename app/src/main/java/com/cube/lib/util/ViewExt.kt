package com.cube.lib.util

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView

/**
 * Extension functions for View class
 */

fun <T : View> Activity.bind(@IdRes idRes: Int): Lazy<T>
{
	@Suppress("UNCHECKED_CAST")
	return unsafeLazy { findViewById(idRes) as T }
}

fun <T : View> Fragment.bind(@IdRes idRes: Int): Lazy<T>
{
	@Suppress("UNCHECKED_CAST")
	return unsafeLazy { view?.findViewById(idRes) as T }
}

fun <T : View> View.bind(@IdRes idRes: Int): Lazy<T>
{
	@Suppress("UNCHECKED_CAST")
	return unsafeLazy { findViewById(idRes) as T }
}

fun <T : View> RecyclerView.ViewHolder.bind(@IdRes idRes: Int): Lazy<T>
{
	@Suppress("UNCHECKED_CAST")
	return unsafeLazy { itemView.findViewById(idRes) as T }
}

fun <T : View> bind(@IdRes idRes: Int, parent : View): Lazy<T>
{
	@Suppress("UNCHECKED_CAST")
	return unsafeLazy { parent.findViewById(idRes) as T }
}

/**
 * Convenience method for inflating a view into another. Will return the inflated view, or the parent view if attach = true
 */
fun <T : View> View.inflate(@LayoutRes layoutRes: Int, attach: Boolean = false): T
{
	@Suppress("UNCHECKED_CAST")
	return LayoutInflater.from(context).inflate(layoutRes, this as ViewGroup, attach) as T
}

private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)

/**
 * Tints views based on view type
 */
fun <T : View> T.tint(@ColorRes colourRes: Int): T
{
	val tintColour = resources.getColor(colourRes)
	when (this)
	{
		is ProgressBar ->
		{
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
			{
				indeterminateTintList = ColorStateList.valueOf(tintColour)
				progressTintList = ColorStateList.valueOf(tintColour)
				secondaryProgressTintList = ColorStateList.valueOf(tintColour)
			}
			else
			{
				progressDrawable.tint(tintColour)
				indeterminateDrawable.tint(tintColour)
			}
		}

		is ImageView -> drawable.tint(tintColour)
		is TextView -> compoundDrawables?.tint(tintColour)
		else -> background.tint(tintColour)
	}

	return this
}

/**
 * Tints ann array of drawables
 */
fun Array<Drawable?>.tint(@ColorInt tintColour: Int) = forEach { drawable -> drawable?.tint(tintColour) }

/**
 * Tints a drawable
 */
fun Drawable.tint(@ColorInt tintColour: Int)
{
	mutate()
	colorFilter = PorterDuffColorFilter(tintColour, PorterDuff.Mode.SRC_IN)
}
