package com.cube.arc.workflow.model

/**
 * Recursive module object.
 *
 * Module -> Module Step -> Step sub step -> Tools
 */
data class Module
(
	var id : String,
	var order: String = "",
	var title : String = "",
	var content : String? = null,
	var directories: List<Module> = listOf(),
	var attachments : List<FileDescriptor> = listOf(),
	var critical : Boolean = false
)
