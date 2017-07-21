package com.cube.arc.workflow.view.holder

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.cube.arc.R
import com.cube.arc.workflow.adapter.NoteActivity
import com.cube.arc.workflow.model.Module
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.util.inflate

/**
 * View holder for module in WorkFlowFragment recycler view
 */
class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
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
			stepRoadmap.visibility = if (step.attachments?.filter { file -> file.featured }?.size == 1) View.VISIBLE else View.GONE

			stepsContainer.addView(stepView)
			populateSubSteps(model, step, stepView)
		}

		stepsContainer.visibility = View.GONE
		moduleClickArea.setOnClickListener { view ->
			when
			{
				stepsContainer.visibility == View.VISIBLE -> hideView(stepsContainer)
				else -> showView(stepsContainer)
			}

			chevron.setImageResource(when
			{
				stepsContainer.visibility == View.VISIBLE -> R.drawable.chevron_collapse
				else -> R.drawable.chevron_expand
			})

			roadmap.visibility = if (model.attachments?.filter { file -> file.featured }?.size == 1 && stepsContainer.visibility == View.VISIBLE) View.VISIBLE else View.GONE
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
			val toolView = subStepView.inflate<View>(R.layout.substep_tool_stub)

			val toolTitle = toolView.findViewById(R.id.tool_title) as TextView
			val toolIcon = toolView.findViewById(R.id.tool_icon) as ImageView
			val toolDescription = toolView.findViewById(R.id.tool_description) as TextView
			val toolCheck = toolView.findViewById(R.id.tool_check) as CheckBox
			val critical = toolView.findViewById(R.id.critical_tool) as View
			val note = toolView.findViewById(R.id.note_added) as View
			val exported = toolView.findViewById(R.id.exported) as View
			val options = toolView.findViewById(R.id.options_menu) as ImageButton

			critical.visibility = if (tool.critical) View.VISIBLE else View.GONE

			toolTitle.text = tool.title
			toolDescription.text = tool.content

			toolCheck.isChecked = checkPrefs.contains(tool.id)
			toolCheck.setOnCheckedChangeListener { buttonView, isChecked ->
				checkPrefs.edit().apply {
					when
					{
						isChecked -> putBoolean(tool.id, true)
						else -> remove(tool.id)
					}
				}.apply()
			}

			toolView.setOnClickListener { view ->
				//
			}

			options.setOnClickListener { view ->
				val criticalPrefs = view.context.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)
				val popup = PopupMenu(view.context, view)

				popup.menuInflater.inflate(R.menu.menu_tool, popup.menu)

				if (tool.critical)
				{
					popup.menu.findItem(R.id.action_mark).isVisible = false
				}
				else
				{
					popup.menu.findItem(R.id.action_mark).title = when
					{
						criticalPrefs.contains(tool.id) -> view.resources.getString(R.string.tool_menu_unmark)
						else -> view.resources.getString(R.string.tool_menu_mark)
					}
				}

				popup.show()

				popup.setOnMenuItemClickListener { item ->
					when (item.itemId)
					{
						R.id.action_mark -> criticalPrefs.edit().apply {
							when (criticalPrefs.contains(tool.id))
							{
								false -> {
									putBoolean(tool.id, true)
									critical.visibility = View.VISIBLE
								}
								else -> {
									remove(tool.id)
									critical.visibility = View.GONE
								}
							}
						}.apply()
					}

					true
				}
			}

			toolContainer.addView(toolView, toolContainer.childCount - 1)
		}

		toolContainer.visibility = View.GONE

		subStepView.setOnClickListener {
			when
			{
				toolContainer.visibility == View.VISIBLE -> hideView(toolContainer)
				else -> showView(toolContainer)
			}

			subStepChevron.setImageResource(when
			{
				toolContainer.visibility == View.VISIBLE -> R.drawable.chevron_collapse
				else -> R.drawable.chevron_expand
			})
		}
	}

	fun showView(view: View)
	{
		view.visibility = View.VISIBLE

//		val anim = AnimationUtils.loadAnimation(view.context, R.anim.slide_in_top)
//		anim.setAnimationListener(object : Animation.AnimationListener
//		{
//			override fun onAnimationRepeat(animation: Animation?){}
//			override fun onAnimationStart(animation: Animation?)
//			{
//				view.visibility = View.VISIBLE
//			}
//
//			override fun onAnimationEnd(animation: Animation?){}
//		})
//		view.startAnimation(anim)
	}

	fun hideView(view: View)
	{
		view.visibility = View.GONE

//		val anim = AnimationUtils.loadAnimation(view.context, R.anim.slide_up_bottom)
//		anim.setAnimationListener(object : Animation.AnimationListener
//		{
//			override fun onAnimationRepeat(animation: Animation?){}
//			override fun onAnimationStart(animation: Animation?)
//			{
//				view.visibility = View.VISIBLE
//			}
//
//			override fun onAnimationEnd(animation: Animation?){}
//		})
//		view.startAnimation(anim)
	}
}
