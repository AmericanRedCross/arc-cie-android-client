package com.cube.arc.workflow.model

/**
 * Basic data class for describing attachments in [Module]
 */
data class FileDescriptor
(
	var title : String,
	var url : String,
	var mime : String,
	var size : Long,
	var description: String,
	var featured: Boolean = false
)
