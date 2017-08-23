package com.cube.arc.workflow.manager

import android.content.Context
import com.cube.arc.R
import com.cube.arc.workflow.model.Module
import com.cube.lib.util.flatSteps
import com.cube.lib.util.parent
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Manager class for loading data source into usable list of Modules for easy access
 */
object ModulesManager
{
	// Tree map of the structure,  <Id, Depth>. position in tree will be position in modules list
	lateinit var tree : LinkedHashMap<String, Int>
	lateinit var modules: List<Module>

	val moduleImages = mapOf(
		1 to R.drawable.module_1_backdrop,
		2 to R.drawable.module_2_backdrop,
		3 to R.drawable.module_3_backdrop,
		4 to R.drawable.module_4_backdrop,
		5 to R.drawable.module_5_backdrop
	)

	val moduleColours = mapOf(
		1 to R.color.module_1,
		2 to R.color.module_2,
		3 to R.color.module_3,
		4 to R.color.module_4,
		5 to R.color.module_5
	)

	/**
	 * Initialises the module manager with a file from a given input stream
	 */
	fun init(dataSource: InputStream)
	{
		modules = Gson().fromJson(InputStreamReader(dataSource), object : TypeToken<ArrayList<Module>>(){}.type)

		tree = LinkedHashMap<String, Int>()

		modules.forEach { module ->
			mapTree(module)
		}
	}

	private fun mapTree(module: Module)
	{
		var depth = 0
		var parent: Module? = module

		while (true)
		{
			parent = parent?.parent() ?: break
			depth++
		}

		tree.put(module.id, depth)

		module.steps?.forEach { step ->
			mapTree(step)
		}
	}

	/**
	 * Gets a module or sub step/tool from ID
	 */
	fun module(id: String): Module?
	{
		var found = modules.forEach {
			val result = search(it, id)

			if (result != null) return result
		}

		return null
	}

	/**
	 * Gets a list of critical and/or user critical tools
	 */
	fun modules(onlyCritical: Boolean, includeUserCritical: Boolean = false, context: Context? = null): List<Module>
	{
		val criticalPrefs = context?.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)
		return modules.flatSteps().filter {
			((includeUserCritical && criticalPrefs?.contains(it.id) ?: false) || it.critical) || (onlyCritical && !includeUserCritical && it.critical)
		}
	}

	/**
	 * Searches the given list of modules for a matching ID recursively
	 */
	fun search(module: Module, id: String): Module?
	{
		if (module.id == id)
		{
			return module
		}

		module.steps?.forEach { step ->
			val found = search(step, id)

			if (found != null) return found
		}

		return null
	}

	/**
	 * Searches the given list of modules for a matching ID recursively
	 */
	fun searchParent(id: String): Module?
	{
		modules.forEach { module ->
			searchParent(module, id)?.let { return it }
		}

		return null
	}

	/**
	 * Searches the given list of modules for a matching ID recursively
	 *
	 * @param root The root module to search from
	 */
	fun searchParent(root: Module, id: String): Module?
	{
		root.steps?.forEach { step ->
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
	 * Recursively counts the number of sub-steps for a given module
	 */
	fun subStepCount(module: Module): Int
	{
		return module.steps?.flatMap { it.steps ?: listOf() }?.size ?: 0
	}

	/**
	 * Recursively counts the number of completed tools for a given module
	 */
	fun completedSubStepCount(context: Context, module: Module): Int
	{
		val checkPrefs = context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)
		return module.steps?.flatMap { it.steps ?: listOf() }?.filter { checkPrefs.contains(it.id) }?.size ?: 0
	}

	/**
	 * Recursively counts the number of tools for a given module
	 */
	fun toolCount(module: Module, onlyCritical: Boolean = false): Int
	{
		val subSteps = module.steps?.flatMap { it.steps ?: listOf() }
		val tools = subSteps?.flatMap { it.steps ?: listOf() }
		return tools?.filter { if (onlyCritical) it.critical else true }?.size ?: 0
	}

	/**
	 * Recursively counts the number of completed tools for a given module
	 */
	fun completedToolCount(context: Context, module: Module, onlyCritical: Boolean = false): Int
	{
		val checkPrefs = context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		val subSteps = module.steps?.flatMap { it.steps ?: listOf() }
		val tools = subSteps?.flatMap { it.steps ?: listOf() }
			?.filter { if (onlyCritical) it.critical else true }
			?.filter { checkPrefs.contains(it.id) }

		return tools?.size ?: 0
	}
}
