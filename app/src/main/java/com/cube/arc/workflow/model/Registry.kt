package com.cube.arc.workflow.model

/**
 * Data registry class used to determine if a downloaded [FileDescriptor] is the latest version
 */
data class Registry(
	var fileName: String,
	var timestamp: Long
)
