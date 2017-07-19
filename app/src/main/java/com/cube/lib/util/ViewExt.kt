package com.cube.lib.util

import android.app.Activity
import android.support.annotation.IdRes
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

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
fun <T : View> View.inflate(@LayoutRes layoutRes: Int, attach: Boolean = false) : T
{
	@Suppress("UNCHECKED_CAST")
	return LayoutInflater.from(context).inflate(layoutRes, this as ViewGroup, attach) as T
}

private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
