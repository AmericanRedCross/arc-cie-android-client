package com.cube.arc.workflow.model

/**
 * Recursive module object.
 *
 * Module -> Module Step -> Step sub step -> Tools
 */
data class Module
(
	var id : String,
	var hierarchy : Int = 0,
	var title : String = "",
	var content : String = "",
	var steps : List<Module>? = null,
	var attachments : List<FileDescriptor>? = null,
	var critical : Boolean = false
)
