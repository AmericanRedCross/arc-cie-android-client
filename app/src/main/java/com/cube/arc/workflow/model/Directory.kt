package com.cube.arc.workflow.model

/**
 * Recursive directory object.
 *
 * Directory -> Directory Step -> Step sub step -> Tools
 */
data class Directory
(
	var id : Int,
	var order: Int = 0,
	var title : String = "",
	var content : String? = null,
	var directories: List<Directory> = listOf(),
	var attachments : List<FileDescriptor> = listOf(),
	var metadata : Map<Any?, Any?>? = null
)
