package com.cube.arc.workflow.model

/**
 * Basic model populated by [SearchManager]
 *
 * @author Callum Taylor
 */
data class SearchResult(
	var searchQuery: String,
	var moduleId: String,
	var title: String,
	var content: String,
	var isAttachment: Boolean = false
)
