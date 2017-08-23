package com.cube.lib.util

import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.model.Module

/**
 * Flattens the recursive array to return a 1D list of type [Module] from [Module.directories]
 */
fun List<Module>.flatSteps(): List<Module>
{
	var results = arrayListOf<Module>()

	for (item in this)
	{
		results.add(item)
		results.addAll(item.directories?.flatSteps() ?: listOf())
	}

	return results
}

/**
 * Gets the parent [Module] object, or null if the object is a root object, or could not be found
 */
fun Module.parent(): Module?
{
	return ModulesManager.searchParent(id)
}
