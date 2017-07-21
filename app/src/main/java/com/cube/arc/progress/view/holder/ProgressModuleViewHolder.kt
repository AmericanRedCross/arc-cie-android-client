package com.cube.arc.progress.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.model.Module

/**
 * View holder for displaying the UI of the module progress
 */
class ProgressModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val stepsProgress = itemView.findViewById(R.id.module_substeps_progress_text) as TextView
	private val toolsProgress = itemView.findViewById(R.id.module_critical_progress_text) as TextView
	private val totalProgress = itemView.findViewById(R.id.progress_text) as TextView

	fun populate(model: Module)
	{
		val completedSubSteps = ModulesManager.completedSubStepCount(itemView.context, model)
		val totalSubSteps = ModulesManager.subStepCount(model)
		stepsProgress.text = itemView.resources.getString(R.string.progress_substep_summary, completedSubSteps.toString(), totalSubSteps.toString())

		val completedTools = ModulesManager.completedToolCount(itemView.context, model, true)
		val totalTools = ModulesManager.toolCount(model, true)
		toolsProgress.text = itemView.resources.getString(R.string.progress_tool_summary, completedTools.toString(), totalTools.toString())

		val totalComplete = Math.round((completedTools.toDouble() / totalTools.toDouble()) * 100.0)
		totalProgress.text = itemView.resources.getString(R.string.progress_text, totalComplete.toString())
	}
}
