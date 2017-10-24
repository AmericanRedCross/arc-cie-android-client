package com.cube.arc.workflow.view.holder

import android.content.Context
import android.content.Intent
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.cie.activity.DocumentViewerActivity
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.workflow.activity.NoteActivity
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.util.directoryColours
import com.cube.lib.util.directoryImages
import com.cube.lib.util.inflate
import com.cube.lib.util.tint

/**
 * View holder for directory in WorkFlowFragment recycler view
 */
class DirectoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val stepsContainer = itemView.findViewById(R.id.steps_container) as LinearLayout
	private val directoryClickArea = itemView.findViewById(R.id.directory_click_area)
	private var chevron = itemView.findViewById(R.id.directory_chevron) as ImageView
	private var title = itemView.findViewById(R.id.directory_name) as TextView
	private var image = itemView.findViewById(R.id.directory_image) as ImageView
	private val roadmap = itemView.findViewById(R.id.directory_roadmap) as Button
	private var hierarchy = itemView.findViewById(R.id.directory_hierarchy) as TextView
	private var directoryHierarchy: String = "0"

	fun populate(model: Directory)
	{
		directoryHierarchy = model.metadata?.get("hierarchy") as String? ?: ""

		title.text = model.title
		hierarchy.text = (directoryHierarchy).toString()

		hierarchy.background.tint(hierarchy.resources.getColor(DirectoryManager.directoryColours[model.order] ?: R.color.directory_1))
		image.setImageResource(DirectoryManager.directoryImages[model.order] ?: R.drawable.directory_1_backdrop)

		if (stepsContainer.childCount > 0 || MainApplication.visibilityMap[model.id.toString()] as Boolean? == true)
		{
			// force refresh views
			populateSteps(model)
			chevron.setImageResource(R.drawable.chevron_collapse)
		}

		directoryClickArea.setOnClickListener { view ->
			populateSteps(model)

			when
			{
				stepsContainer.visibility == View.VISIBLE -> {
					AnalyticsHelper.userCollapsesDirectory(model)
					hideView(model, stepsContainer)
				}
				else -> {
					AnalyticsHelper.userExpandsDirectory(model)
					showView(model, stepsContainer)
				}
			}

			chevron.setImageResource(when
			{
				stepsContainer.visibility == View.VISIBLE -> R.drawable.chevron_collapse
				else -> R.drawable.chevron_expand
			})

			roadmap.visibility = if (model.content != null && stepsContainer.visibility == View.VISIBLE) View.VISIBLE else View.GONE
			roadmap.setOnClickListener { view ->
				AnalyticsHelper.userTapsDirectoryRoadmap(model)
				IntentDataHelper.store(DocumentViewerActivity::class.java, model)
				view.context.startActivity(Intent(view.context, DocumentViewerActivity::class.java))
			}
		}
	}

	private fun populateSteps(model: Directory)
	{
		stepsContainer.removeAllViews()

		model.directories.forEach { step ->
			val stepView = stepsContainer.inflate<View>(R.layout.directory_step_stub)

			val stepHierarchy = stepView.findViewById(R.id.step_hierarchy) as TextView
			val stepTitle = stepView.findViewById(R.id.step_title) as TextView
			val stepRoadmap = stepView.findViewById(R.id.step_roadmap) as Button

			stepHierarchy.text = step.metadata?.get("hierarchy") as String? ?: ""
			stepTitle.text = step.title

			stepRoadmap.visibility = if (step.content != null) View.VISIBLE else View.GONE
			stepRoadmap.setOnClickListener { view ->
				AnalyticsHelper.userTapsDirectoryRoadmap(model)
				IntentDataHelper.store(DocumentViewerActivity::class.java, step)
				view.context.startActivity(Intent(view.context, DocumentViewerActivity::class.java))
			}

			stepsContainer.addView(stepView)
			populateSubSteps(model, step, stepView)
		}

		restoreVisibility(model, stepsContainer)
	}

	private fun populateSubSteps(root: Directory, step: Directory, stepView: View)
	{
		val subStepContainer = stepView.findViewById(R.id.substeps_container) as ViewGroup
		val notePrefs = stepView.context.getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
		val checkPrefs = stepView.context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		step.directories.forEach { subStep ->
			val subStepView = subStepContainer.inflate<View>(R.layout.step_substep_stub)

			val subStepCheck = subStepView.findViewById(R.id.substep_check) as AppCompatCheckBox
			val subStepHierarchy = subStepView.findViewById(R.id.substep_hierarchy) as TextView
			val subStepTitle = subStepView.findViewById(R.id.substep_title) as TextView
			val subStepNoteButton = subStepView.findViewById(R.id.add_note) as Button

			restoreVisibility(subStep, subStepNoteButton)
			subStepHierarchy.text = subStep.metadata?.get("hierarchy") as String? ?: ""
			subStepTitle.text = subStep.title
			subStepNoteButton.setOnClickListener { view ->
				when
				{
					notePrefs.contains(subStep.id.toString()) -> AnalyticsHelper.userTapsEditNote(subStep)
					else -> AnalyticsHelper.userTapsAddNote(subStep)
				}

				val noteIntent = Intent(view.context, NoteActivity::class.java)
				IntentDataHelper.store(NoteActivity::class.java, subStep.id)
				view.context.startActivity(noteIntent)
			}

			subStepCheck.tint(DirectoryManager.directoryColours[root.order] ?: R.color.directory_1)
			subStepCheck.isChecked = checkPrefs.contains(subStep.id.toString())
			subStepCheck.setOnCheckedChangeListener { buttonView, isChecked ->
				checkPrefs.edit().apply {
					when
					{
						isChecked -> {
							AnalyticsHelper.userChecksDirectoryCheckbox(subStep)
							putBoolean(subStep.id.toString(), true)
						}
						else -> {
							AnalyticsHelper.userUnchecksDirectoryCheckbox(subStep)
							remove(subStep.id.toString())
						}
					}
				}.apply()
			}

			subStepNoteButton.setText(when
			{
				notePrefs.contains(subStep.id.toString()) -> R.string.directory_substep_edit_note
				else -> R.string.directory_substep_add_note
			})

			subStepContainer.addView(subStepView)

			subStepView.setOnClickListener {
				var toolContainer = subStepView.findViewById(R.id.tools_container) as ViewGroup
				if (toolContainer.childCount - 2 > 0)
				{
					toolContainer.removeViews(1, toolContainer.childCount - 2)
				}

				when
				{
					toolContainer.visibility == View.VISIBLE -> {
						AnalyticsHelper.userCollapsesDirectory(subStep)

						hideView(subStep, toolContainer)
						hideView(subStep, subStepView.findViewById(R.id.add_note))
					}
					else -> {
						AnalyticsHelper.userExpandsDirectory(subStep)

						populateSubStepTools(root, step, subStep, subStepView)
						showView(subStep, toolContainer)
						showView(subStep, subStepView.findViewById(R.id.add_note))
					}
				}

				var subStepChevron = subStepView.findViewById(R.id.substep_chevron) as ImageView
				subStepChevron.setImageResource(when
				{
					toolContainer.visibility == View.VISIBLE -> R.drawable.chevron_collapse
					else -> R.drawable.chevron_expand
				})
			}

			if (MainApplication.visibilityMap[subStep.id.toString()] as Boolean? == true)
			{
				subStepView.performClick()
			}
		}
	}

	private fun populateSubStepTools(root: Directory, step: Directory, subStep: Directory, subStepView: View)
	{
		var toolContainer = subStepView.findViewById(R.id.tools_container) as ViewGroup
		var checkPrefs = subStepView.context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		toolContainer.getChildAt(0).tint(DirectoryManager.directoryColours[root.order] ?: R.color.directory_1)
		toolContainer.getChildAt(1).tint(DirectoryManager.directoryColours[root.order] ?: R.color.directory_1)
		toolContainer.tint(DirectoryManager.directoryColours[root.order] ?: R.color.directory_1, 0.2f)

		subStep.directories.forEach { tool ->
			val toolViewHolder = ToolViewHolder(subStepView.inflate<View>(R.layout.substep_tool_stub))
			toolViewHolder.populate(root, tool)

			toolContainer.addView(toolViewHolder.itemView, toolContainer.childCount - 1)
		}
	}

	fun restoreVisibility(directory: Directory, view: View)
	{
		view.visibility = if (MainApplication.visibilityMap[directory.id.toString()] as Boolean? == true) View.VISIBLE else View.GONE
	}

	fun showView(directory: Directory, view: View)
	{
		view.visibility = View.VISIBLE
		MainApplication.visibilityMap[directory.id.toString()] = true // expanded
	}

	fun hideView(directory: Directory, view: View)
	{
		view.visibility = View.GONE
		MainApplication.visibilityMap[directory.id.toString()] = false // collapsed
	}
}
