package com.cube.arc.workflow.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.workflow.model.Module
import com.cube.lib.util.bind
import com.cube.lib.util.inflate

/**
 * View holder for module in WorkFlowFragment recycler view
 */
class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val stepsContainer by bind<LinearLayout>(R.id.steps_container, itemView)
	private var title: TextView = itemView.findViewById(R.id.module_name) as TextView
	private var hierarchy: TextView = itemView.findViewById(R.id.module_hierarchy) as TextView

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

			stepHierarchy.text = "${model.hierarchy}.${step.hierarchy}"
			stepTitle.text = step.title

			populateSubSteps(model, step, stepView)

			stepsContainer.addView(stepView)
		}

		stepsContainer.visibility = View.GONE
		itemView.setOnClickListener { view ->
			stepsContainer.visibility = when
			{
				stepsContainer.visibility == View.VISIBLE -> View.GONE
				else -> View.VISIBLE
			}
		}
	}

	private fun populateSubSteps(root: Module, step: Module, stepView: View)
	{
		var subStepContainer = stepView.findViewById(R.id.substeps_container) as ViewGroup

		step.steps?.forEach { subStep ->
			val subStepView = subStepContainer.inflate<View>(R.layout.step_substep_stub)

			val subStepHierarchy = subStepView.findViewById(R.id.substep_hierarchy) as TextView
			val subStepTitle = subStepView.findViewById(R.id.substep_title) as TextView

			subStepHierarchy.text = "${root.hierarchy}.${step.hierarchy}.${subStep.hierarchy}"
			subStepTitle.text = subStep.title

			populateSubStepTools(root, step, subStep, subStepView)

			subStepContainer.addView(subStepView)
		}
	}

	private fun populateSubStepTools(root: Module, step: Module, subStep: Module, subStepView: View)
	{
		var toolContainer = subStepView.findViewById(R.id.tools_container) as ViewGroup

		subStep.steps?.forEach { tool ->
			val toolView = subStepView.inflate<View>(R.layout.substep_tool_stub)

			val toolTitle = toolView.findViewById(R.id.tool_title) as TextView
			val toolIcon = toolView.findViewById(R.id.tool_icon) as ImageView
			val toolDescription = toolView.findViewById(R.id.tool_description) as TextView

			toolTitle.text = tool.title
			toolDescription.text = tool.content

			toolContainer.addView(toolView, toolContainer.childCount - 1)
		}

		toolContainer.visibility = View.GONE

		subStepView.setOnClickListener {
			toolContainer.visibility = when
			{
				toolContainer.visibility == View.VISIBLE -> View.GONE
				else -> View.VISIBLE
			}
		}
	}
}
