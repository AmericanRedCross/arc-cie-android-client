package com.cube.lib.util

import android.app.Activity
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.View

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

private fun <T> unsafeLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE, initializer)
