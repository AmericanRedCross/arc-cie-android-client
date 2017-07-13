package com.cube.arc.workflow.model

/**
 * Top level module data struct
 */
data class Module
(
	var id : String,
	var module : Int,
	var title : String,
	var roadmap : String,
	var steps : List<ModuleStep>
)
