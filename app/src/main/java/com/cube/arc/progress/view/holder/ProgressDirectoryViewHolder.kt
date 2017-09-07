package com.cube.arc.progress.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.workflow.manager.DirectoriesManager
import com.cube.arc.workflow.model.Directory
import com.cube.lib.util.tint

/**
 * View holder for displaying the UI of the directory progress
 */
class ProgressDirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val name = itemView.findViewById(R.id.directory_name) as TextView
	private val hierarchy = itemView.findViewById(R.id.directory_hierarchy) as TextView
	private val stepsProgress = itemView.findViewById(R.id.directory_substeps_progress_text) as TextView
	private val toolsProgress = itemView.findViewById(R.id.directory_critical_progress_text) as TextView
	private val totalProgress = itemView.findViewById(R.id.progress_text) as TextView
	private val directoryProgress = itemView.findViewById(R.id.directory_progress) as ProgressBar
	private val subStepsDivider = itemView.findViewById(R.id.substeps_divider) as View
	private val criticalDivider = itemView.findViewById(R.id.critical_divider) as View

	fun populate(model: Directory)
	{
		hierarchy.background.tint(hierarchy.resources.getColor(DirectoriesManager.directoryColours[model.order] ?: R.color.directory_1))
		directoryProgress.tint(DirectoriesManager.directoryColours[model.order] ?: R.color.directory_1)
		subStepsDivider.tint(DirectoriesManager.directoryColours[model.order] ?: R.color.directory_1)
		criticalDivider.tint(DirectoriesManager.directoryColours[model.order] ?: R.color.directory_1)

		val completedSubSteps = DirectoriesManager.completedSubStepCount(itemView.context, model)
		val totalSubSteps = DirectoriesManager.subStepCount(model)
		stepsProgress.text = itemView.resources.getString(R.string.progress_substep_summary, completedSubSteps.toString(), totalSubSteps.toString())

		val completedTools = DirectoriesManager.completedToolCount(itemView.context, model, true)
		val totalTools = DirectoriesManager.toolCount(model, true)
		toolsProgress.text = itemView.resources.getString(R.string.progress_tool_summary, completedTools.toString(), totalTools.toString())

		val totalComplete = Math.round((completedTools.toDouble() / totalTools.toDouble()) * 100.0).toInt()
		totalProgress.text = itemView.resources.getString(R.string.progress_text, totalComplete.toString())

		directoryProgress.progress = totalComplete
		name.text = model.title
		hierarchy.text = model.order.toString()
	}
}
