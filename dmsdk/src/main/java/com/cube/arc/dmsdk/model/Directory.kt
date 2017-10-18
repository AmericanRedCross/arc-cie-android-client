package com.cube.arc.dmsdk.model

/**
 * Recursive directory object.
 */
data class Directory
(
	/**
	 * Identifier of the directory object
	 */
	var id : Int,

	/**
	 * Identifier of the parent directory object
	 */
	var parentId : Int? = null,

	/**
	 * The order of the directory, 0-based index
	 */
	var order: Int = 0,

	/**
	 * Non-null title of directory
	 */
	var title : String = "",

	/**
	 * Either inline, or remote file reference to content for the given directory
	 */
	var content : String? = null,

	/**
	 * List of sub directories. can be empty
	 */
	var directories: List<Directory> = listOf(),

	/**
	 * List of attached associated files to the directory
	 */
	var attachments : List<FileDescriptor> = listOf(),

	/**
	 * A map of type [Any] various meta data. can be null
	 */
	var metadata : Map<String, Any?>? = null
)

/**
 * Flattens the recursive array to return a 1D list of type [Directory] from [Directory.directories]
 */
public fun List<Directory>.flat(): List<Directory>
{
	var results = arrayListOf<Directory>()

	for (item in this)
	{
		results.add(item)
		results.addAll(item.directories.flat())
	}

	return results
}
