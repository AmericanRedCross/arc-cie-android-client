package com.cube.arc.workflow.model

/**
 * // TODO: Add class description
 */
data class FileDescriptor
(
	var title : String,
	var url : String,
	var critical : Boolean = false,
	var mime : String
)
