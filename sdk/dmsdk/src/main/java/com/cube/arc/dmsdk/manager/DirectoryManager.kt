package com.cube.arc.dmsdk.manager

import com.cube.arc.dmsdk.model.Directory
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Manager class for loading data source into usable list of [Directory] for easy access
 */
object DirectoryManager
{
	/**
	 * Internal list of [Directory] loaded via [init]. Do not set this via assign, do so via any [init] method
	 */
	var directories: ArrayList<Directory> = arrayListOf()
		set(value){}

	/**
	 * Initialises the manager instance with a stream to a file to decode into [directories]
	 */
	public fun init(dataSource: InputStream)
	{
		directories.clear()
		directories.addAll(Gson().fromJson(InputStreamReader(dataSource), object : TypeToken<ArrayList<Directory>?>(){}.type) ?: arrayListOf<Directory>())
	}

	/**
	 * Initialises the manager instance with a String representation of [directories]
	 */
	public fun init(dataSource: String)
	{
		directories.clear()
		directories.addAll(Gson().fromJson(dataSource, object : TypeToken<ArrayList<Directory>?>(){}.type) ?: arrayListOf<Directory>())
	}

	/**
	 * Initialises the manager instance with a [JsonElement] object
	 */
	public fun init(dataSource: JsonElement)
	{
		directories.clear()
		directories.addAll(Gson().fromJson(dataSource, object : TypeToken<ArrayList<Directory>?>(){}.type) ?: arrayListOf<Directory>())
	}

	/**
	 * Initialises the manager instance with a list of [Directory] models
	 */
	public fun init(dataSource: List<Directory>)
	{
		directories.clear()
		directories.addAll(dataSource)
	}

	/**
	 * Gets a directory for a given [id], or `null` if one was not found
	 */
	public fun directory(id: Int?, subList: List<Directory> = directories): Directory?
	{
		subList.forEach { subDirectory ->
			if (subDirectory.id == id) return subDirectory

			directory(id, subDirectory.directories)?.let {
				return it
			}
		}

		return null
	}

	/**
	 * Gets the parent [Directory] object, or null if the object is a root object, or could not be found
	 */
	public fun parent(directory: Directory): Directory? = directory(directory.parentId)
}
