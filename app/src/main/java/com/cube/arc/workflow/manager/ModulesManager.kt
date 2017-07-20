package com.cube.arc.workflow.manager

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
		return search(modules, id)
	}

	/**
	 * Searches the given list of modules for a matching ID recursively
	 */
	fun search(modules: List<Module>?, id: String): Module?
	{
		modules?.forEach { module ->
			return if (module.id == id) module else search(module.steps, id)
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
}
