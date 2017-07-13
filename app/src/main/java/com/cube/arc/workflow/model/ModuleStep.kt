package com.cube.arc.workflow.model

/**
 * Data struct for a module step
 */
data class ModuleStep
(
	var id : String,
	var step : Int,
	var roadmap : String,
	var steps : List<StepSubStep>
)
