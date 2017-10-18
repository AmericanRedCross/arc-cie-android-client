package com.cube.arc.dmsdk.model

/**
 * Basic data class for describing attachments in [Directory]
 */
data class FileDescriptor
(
	/**
	 * Title of the attached document
	 */
	var title : String = "",

	/**
	 * Permalink reference to the attached file
	 */
	var url : String = "",

	/**
	 * Mime type of the file
	 */
	var mime : String = "",

	/**
	 * Size (in bytes) of the file
	 */
	var size : Long = 0,

	/**
	 * Nullable description of the file
	 */
	var description: String? = null
)
