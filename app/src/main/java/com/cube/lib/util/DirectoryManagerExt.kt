package com.cube.lib.util

import android.content.Context
import com.cube.arc.R
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.dmsdk.model.flat

/**
 * Extension Manager class for directory manager
 */

val DirectoryManager.directoryImages: Map<Int, Int>
	get() = mapOf(
		0 to R.drawable.directory_1_backdrop,
		1 to R.drawable.directory_2_backdrop,
		2 to R.drawable.directory_3_backdrop,
		3 to R.drawable.directory_4_backdrop,
		4 to R.drawable.directory_5_backdrop
	)

val DirectoryManager.directoryColours: Map<Int, Int>
	get() = mapOf(
		0 to R.color.directory_1,
		1 to R.color.directory_2,
		2 to R.color.directory_3,
		3 to R.color.directory_4,
		4 to R.color.directory_5
	)

/**
 * Gets a list of critical and/or user critical tools
 */
fun DirectoryManager.criticalDirectories(onlyCritical: Boolean, includeUserCritical: Boolean = false, context: Context? = null): List<Directory>
{
	val criticalPrefs = context?.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)
	return DirectoryManager.directories.flat().filter {
		((includeUserCritical && criticalPrefs?.contains(it.id.toString()) ?: false) || (it.metadata?.get("critical_path") as Boolean? ?: false)) || (onlyCritical && !includeUserCritical && (it.metadata?.get("critical_path") as Boolean? ?: false))
	}
}

/**
 * Recursively counts the number of sub-directories for a given directory
 */
fun DirectoryManager.subStepCount(directory: Directory): Int
{
	return directory.directories.flatMap { it.directories ?: listOf() }.size
}

/**
 * Recursively counts the number of completed tools for a given directory
 */
fun DirectoryManager.completedSubStepCount(context: Context, directory: Directory): Int
{
	val checkPrefs = context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)
	return directory.directories.flatMap { it.directories ?: listOf() }.filter { checkPrefs.contains(it.id.toString()) }.size
}

/**
 * Recursively counts the number of tools for a given directory
 */
fun DirectoryManager.toolCount(directory: Directory, onlyCritical: Boolean = false): Int
{
	val subSteps = directory.directories.flatMap { it.directories ?: listOf() }
	val tools = subSteps.flatMap { it.directories ?: listOf() }
	return tools.filter { if (onlyCritical) (it.metadata?.getOrElse("critical_path", { false }) as Boolean ?: false) else true }.size
}

/**
 * Recursively counts the number of completed tools for a given directory
 */
fun DirectoryManager.completedToolCount(context: Context, directory: Directory, onlyCritical: Boolean = false): Int
{
	val checkPrefs = context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

	val subSteps = directory.directories.flatMap { it.directories ?: listOf() }
	val tools = subSteps.flatMap { it.directories ?: listOf() }
		.filter { if (onlyCritical) (it.metadata?.getOrElse("critical_path", { false }) as Boolean ?: false) else true }
		.filter { checkPrefs.contains(it.id.toString()) }

	return tools.size
}
