package com.cube.arc.workflow.model

/**
 * Basic model populated by [SearchManager]
 *
 * @author Callum Taylor
 */
data class SearchResult(
	var searchQuery: String,
	var directoryId: Int,
	var title: String
)
