package com.cube.arc.workflow.view.holder

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cube.arc.R
import com.cube.arc.cie.activity.DocumentViewerActivity
import com.cube.arc.workflow.activity.NoteActivity
import com.cube.arc.workflow.model.Module
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.util.inflate

/**
 * View holder for module in WorkFlowFragment recycler view
 */
class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val visibilityMap = HashMap<String, Boolean>()
	private val stepsContainer = itemView.findViewById(R.id.steps_container) as LinearLayout
	private val moduleClickArea = itemView.findViewById(R.id.module_click_area)
	private var chevron = itemView.findViewById(R.id.module_chevron) as ImageView
	private var title = itemView.findViewById(R.id.module_name) as TextView
	private val roadmap = itemView.findViewById(R.id.module_roadmap) as Button
	private var hierarchy = itemView.findViewById(R.id.module_hierarchy) as TextView

	fun populate(model: Module)
	{
		title.text = model.title
		hierarchy.text = "${model.hierarchy}"

		populateSteps(model)
	}

	private fun populateSteps(model: Module)
	{
		stepsContainer.removeAllViews()

		model.steps?.forEach { step ->
			val stepView = stepsContainer.inflate<View>(R.layout.module_step_stub)

			val stepHierarchy = stepView.findViewById(R.id.step_hierarchy) as TextView
			val stepTitle = stepView.findViewById(R.id.step_title) as TextView
			val stepRoadmap = stepView.findViewById(R.id.step_roadmap) as Button

			stepHierarchy.text = "${model.hierarchy}.${step.hierarchy}"
			stepTitle.text = step.title

			val featuredAttachments = step.attachments?.filter { file -> file.featured }

			stepRoadmap.visibility = if (featuredAttachments?.size == 1) View.VISIBLE else View.GONE
			stepRoadmap.setOnClickListener { view ->
				IntentDataHelper.store(DocumentViewerActivity::class.java, step)
				view.context.startActivity(Intent(view.context, DocumentViewerActivity::class.java))
			}

			stepsContainer.addView(stepView)
			populateSubSteps(model, step, stepView)
		}

		restoreVisibility(model, stepsContainer)
		moduleClickArea.setOnClickListener { view ->
			when
			{
				stepsContainer.visibility == View.VISIBLE -> hideView(model, stepsContainer)
				else -> showView(model, stepsContainer)
			}

			chevron.setImageResource(when
			{
				stepsContainer.visibility == View.VISIBLE -> R.drawable.chevron_collapse
				else -> R.drawable.chevron_expand
			})

			val featuredAttachments = model.attachments?.filter { file -> file.featured }
			roadmap.visibility = if (featuredAttachments?.size == 1 && stepsContainer.visibility == View.VISIBLE) View.VISIBLE else View.GONE
			roadmap.setOnClickListener { view ->
				IntentDataHelper.store(DocumentViewerActivity::class.java, model)
				view.context.startActivity(Intent(view.context, DocumentViewerActivity::class.java))
			}
		}
	}

	private fun populateSubSteps(root: Module, step: Module, stepView: View)
	{
		var subStepContainer = stepView.findViewById(R.id.substeps_container) as ViewGroup
		var notePrefs = stepView.context.getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
		var checkPrefs = stepView.context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		step.steps?.forEach { subStep ->
			val subStepView = subStepContainer.inflate<View>(R.layout.step_substep_stub)

			val subStepCheck = subStepView.findViewById(R.id.substep_check) as CheckBox
			val subStepHierarchy = subStepView.findViewById(R.id.substep_hierarchy) as TextView
			val subStepTitle = subStepView.findViewById(R.id.substep_title) as TextView
			val subStepNoteButton = subStepView.findViewById(R.id.add_note) as Button

			subStepHierarchy.text = "${root.hierarchy}.${step.hierarchy}.${subStep.hierarchy}"
			subStepTitle.text = subStep.title
			subStepNoteButton.setOnClickListener { view ->
				val noteIntent = Intent(view.context, NoteActivity::class.java)
				IntentDataHelper.store(NoteActivity::class.java, step.id)
				view.context.startActivity(noteIntent)
			}

			subStepCheck.isChecked = checkPrefs.contains(subStep.id)
			subStepCheck.setOnCheckedChangeListener { buttonView, isChecked ->
				checkPrefs.edit().apply {
					when
					{
						isChecked -> putBoolean(subStep.id, true)
						else -> remove(subStep.id)
					}
				}.apply()
			}

			subStepNoteButton.setText(when
			{
				notePrefs.contains(step.id) -> R.string.module_substep_edit_note
				else -> R.string.module_substep_add_note
			})

			subStepContainer.addView(subStepView)
			populateSubStepTools(root, step, subStep, subStepView)
		}
	}

	private fun populateSubStepTools(root: Module, step: Module, subStep: Module, subStepView: View)
	{
		var toolContainer = subStepView.findViewById(R.id.tools_container) as ViewGroup
		var subStepChevron = subStepView.findViewById(R.id.substep_chevron) as ImageView
		var checkPrefs = subStepView.context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		subStep.steps?.forEach { tool ->
			val toolViewHolder = ToolViewHolder(subStepView.inflate<View>(R.layout.substep_tool_stub))
			toolViewHolder.populate(tool)

			toolContainer.addView(toolViewHolder.itemView, toolContainer.childCount - 1)
		}

		restoreVisibility(subStep, toolContainer)

		subStepView.setOnClickListener {
			when
			{
				toolContainer.visibility == View.VISIBLE -> hideView(subStep, toolContainer)
				else -> showView(subStep, toolContainer)
			}

			subStepChevron.setImageResource(when
			{
				toolContainer.visibility == View.VISIBLE -> R.drawable.chevron_collapse
				else -> R.drawable.chevron_expand
			})
		}
	}

	fun restoreVisibility(module: Module, view: View)
	{
		view.visibility = if (visibilityMap[module.id] as Boolean? ?: false) View.VISIBLE else View.GONE
	}

	fun showView(module: Module, view: View)
	{
		view.visibility = View.VISIBLE
		visibilityMap[module.id] = true // expanded
	}

	fun hideView(module: Module, view: View)
	{
		view.visibility = View.GONE
		visibilityMap[module.id] = false // collapsed
	}
}
