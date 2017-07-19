package com.cube.arc.workflow.model

/**
 * Recursive module object.
 *
 * Module -> Module Step -> Step sub step -> Tools
 */
data class Module
(
	var id : String,
	var hierarchy : Int,
	var title : String,
	var content : String,
	var steps : List<Module>?,
	var attachments : List<FileDescriptor>?,
	var critical : Boolean = false
)
