package com.cube.arc.workflow.manager

import android.content.Context
import com.cube.arc.workflow.model.Module
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Manager class for loading data source into usable list of Modules for easy access
 */
object ModulesManager
{
	lateinit var modules: List<Module>

	fun init(dataSource: InputStream)
	{
		modules = Gson().fromJson(InputStreamReader(dataSource), object : TypeToken<ArrayList<Module>>(){}.type)
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
