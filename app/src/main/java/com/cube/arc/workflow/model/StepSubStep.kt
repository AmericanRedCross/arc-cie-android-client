package com.cube.arc.workflow.model

/**
 * Data struct for module step sub steps
 */
data class StepSubStep
(
	var id : String,
	var step : Int,
	var files : List<FileDescriptor>
)
