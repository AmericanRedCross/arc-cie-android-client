package com.cube.arc.workflow.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.workflow.model.Module

/**
 * View holder for module in WorkFlowFragment recycler view
 */
class ToolDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val title = itemView.findViewById(R.id.substep_title) as TextView
	private val hierarchy = itemView.findViewById(R.id.substep_hierarchy) as TextView

	fun populate(parent: Module)
	{
		hierarchy.text = parent.hierarchy.toString()
		title.text = parent.title
	}
}
