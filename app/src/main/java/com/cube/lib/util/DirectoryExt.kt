package com.cube.lib.util

import com.cube.arc.workflow.manager.DirectoriesManager
import com.cube.arc.workflow.model.Directory

/**
 * Flattens the recursive array to return a 1D list of type [Directory] from [Directory.directories]
 */
fun List<Directory>.flatSteps(): List<Directory>
{
	var results = arrayListOf<Directory>()

	for (item in this)
	{
		results.add(item)
		results.addAll(item.directories.flatSteps())
	}

	return results
}

/**
 * Gets the parent [Directory] object, or null if the object is a root object, or could not be found
 */
fun Directory.parent(): Directory?
{
	return DirectoriesManager.searchParent(id)
}
