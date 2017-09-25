package com.cube.arc.workflow.manager

import android.content.Context
import com.cube.arc.R
import com.cube.arc.workflow.model.Directory
import com.cube.lib.util.flatSteps
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Manager class for loading data source into usable list of [Directory] for easy access
 */
object DirectoriesManager
{
	lateinit var directories: List<Directory>

	val directoryImages = mapOf(
		0 to R.drawable.directory_1_backdrop,
		1 to R.drawable.directory_2_backdrop,
		2 to R.drawable.directory_3_backdrop,
		3 to R.drawable.directory_4_backdrop,
		4 to R.drawable.directory_5_backdrop
	)

	val directoryColours = mapOf(
		0 to R.color.directory_1,
		1 to R.color.directory_2,
		2 to R.color.directory_3,
		3 to R.color.directory_4,
		4 to R.color.directory_5
	)

	/**
	 * Initialises the directory manager with a file from a given input stream
	 */
	fun init(dataSource: InputStream)
	{
		try
		{
			directories = Gson().fromJson(InputStreamReader(dataSource), object : TypeToken<ArrayList<Directory>>(){}.type)
		}
		catch (e: Exception)
		{
			// failed to parse directories json
			directories = listOf()
		}
	}

	/**
	 * Gets a directory or sub step/tool from ID
	 */
	fun directory(id: Int): Directory?
	{
		var found = directories.forEach {
			val result = search(it, id)

			if (result != null) return result
		}

		return null
	}

	/**
	 * Gets a list of critical and/or user critical tools
	 */
	fun directories(onlyCritical: Boolean, includeUserCritical: Boolean = false, context: Context? = null): List<Directory>
	{
		val criticalPrefs = context?.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)
		return directories.flatSteps().filter {
			((includeUserCritical && criticalPrefs?.contains(it.id.toString()) ?: false) || (it.metadata?.getOrElse("critical_path", { false }) as Boolean ?: false)) || (onlyCritical && !includeUserCritical && (it.metadata?.get("critical_path") as Boolean ?: false))
		}
	}

	/**
	 * Searches the given list of directories for a matching ID recursively
	 */
	fun search(directory: Directory, id: Int): Directory?
	{
		if (directory.id == id)
		{
			return directory
		}

		directory.directories.forEach { step ->
			val found = search(step, id)

			if (found != null) return found
		}

		return null
	}

	/**
	 * Searches the given list of directories for a matching ID recursively
	 */
	fun searchParent(id: Int): Directory?
	{
		directories.forEach { directory ->
			searchParent(directory, id)?.let { return it }
		}

		return null
	}

	/**
	 * Searches the given list of directories for a matching ID recursively
	 *
	 * @param root The root directory to search from
	 */
	fun searchParent(root: Directory, id: Int): Directory?
	{
		root.directories.forEach { step ->
			if (step.id == id)
			{
				return root
			}
			else
			{
				searchParent(step, id)?.let { return it }
			}
		}

		return null
	}

	/**
	 * Recursively counts the number of sub-directories for a given directory
	 */
	fun subStepCount(directory: Directory): Int
	{
		return directory.directories.flatMap { it.directories ?: listOf() }.size
	}

	/**
	 * Recursively counts the number of completed tools for a given directory
	 */
	fun completedSubStepCount(context: Context, directory: Directory): Int
	{
		val checkPrefs = context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)
		return directory.directories.flatMap { it.directories ?: listOf() }.filter { checkPrefs.contains(it.id.toString()) }.size
	}

	/**
	 * Recursively counts the number of tools for a given directory
	 */
	fun toolCount(directory: Directory, onlyCritical: Boolean = false): Int
	{
		val subSteps = directory.directories.flatMap { it.directories ?: listOf() }
		val tools = subSteps.flatMap { it.directories ?: listOf() }
		return tools.filter { if (onlyCritical) (it.metadata?.getOrElse("critical_path", { false }) as Boolean ?: false) else true }.size
	}

	/**
	 * Recursively counts the number of completed tools for a given directory
	 */
	fun completedToolCount(context: Context, directory: Directory, onlyCritical: Boolean = false): Int
	{
		val checkPrefs = context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		val subSteps = directory.directories.flatMap { it.directories ?: listOf() }
		val tools = subSteps.flatMap { it.directories ?: listOf() }
			.filter { if (onlyCritical) (it.metadata?.getOrElse("critical_path", { false }) as Boolean ?: false) else true }
			.filter { checkPrefs.contains(it.id.toString()) }

		return tools.size
	}
}
