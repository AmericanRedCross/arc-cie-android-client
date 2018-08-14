package com.cube.arc.workflow.view.holder

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.dmsdk.model.Directory
import com.cube.lib.util.bind

/**
 * View holder for directory in WorkFlowFragment recycler view
 */
class ToolDividerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
    private val title by bind<TextView>(R.id.substep_title)
	private val hierarchy by bind<TextView>(R.id.substep_hierarchy)

	fun populate(parent: Directory)
	{

		hierarchy.text = parent.metadata?.getOrElse("hierarchy", { null }) as String? ?: ""
		title.text = parent.title
	}
}
