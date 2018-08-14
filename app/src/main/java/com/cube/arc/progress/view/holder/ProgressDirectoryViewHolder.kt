package com.cube.arc.progress.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.lib.util.*

/**
 * View holder for displaying the UI of the directory progress
 */
class ProgressDirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val name by bind<TextView>(R.id.directory_name)
	private val hierarchy by bind<TextView>(R.id.directory_hierarchy)
	private val stepsProgress by bind<TextView>(R.id.directory_substeps_progress_text)
	private val toolsProgress by bind<TextView>(R.id.directory_critical_progress_text)
	private val totalProgress by bind<TextView>(R.id.progress_text)
	private val directoryProgress by bind<ProgressBar>(R.id.directory_progress)
	private val subStepsDivider by bind<View>(R.id.substeps_divider)
	private val criticalDivider by bind<View>(R.id.critical_divider)

	fun populate(model: Directory)
	{
		hierarchy.background.tint(hierarchy.context.getColorCompat(DirectoryManager.directoryColours[model.order] ?: R.color.directory_1))
		directoryProgress.tint(DirectoryManager.directoryColours[model.order] ?: R.color.directory_1)
		subStepsDivider.tint(DirectoryManager.directoryColours[model.order] ?: R.color.directory_1)
		criticalDivider.tint(DirectoryManager.directoryColours[model.order] ?: R.color.directory_1)

		val completedSubSteps = DirectoryManager.completedSubStepCount(itemView.context, model)
		val totalSubSteps = DirectoryManager.subStepCount(model)
		stepsProgress.text = itemView.resources.getString(R.string.progress_substep_summary, completedSubSteps.toString(), totalSubSteps.toString())

		val completedTools = DirectoryManager.completedToolCount(itemView.context, model, true)
		val totalTools = DirectoryManager.toolCount(model, true)
		toolsProgress.text = itemView.resources.getString(R.string.progress_tool_summary, completedTools.toString(), totalTools.toString())

		val totalComplete = Math.round((completedTools.toDouble() / totalTools.toDouble()) * 100.0).toInt()
		totalProgress.text = itemView.resources.getString(R.string.progress_text, totalComplete.toString())

		directoryProgress.progress = totalComplete
		name.text = model.title
		hierarchy.text = model.metadata?.getOrElse("hierarchy", { null }) as String? ?: ""
	}
}
