package com.cube.arc.workflow.model

/**
 * Basic data class for describing attachments in [Directory]
 */
data class FileDescriptor
(
	var title : String = "",
	var url : String = "",
	var mime : String = "",
	var size : Long = 0,
	var description: String = "",
	var timestamp: Long = 0,
	var featured: Boolean = false
)
