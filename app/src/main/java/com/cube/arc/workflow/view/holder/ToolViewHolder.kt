package com.cube.arc.workflow.view.holder

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.workflow.model.Module

/**
 * View holder for module in WorkFlowFragment recycler view
 */
class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val toolTitle = itemView.findViewById(R.id.tool_title) as TextView
	private val toolIcon = itemView.findViewById(R.id.tool_icon) as ImageView
	private val toolDescription = itemView.findViewById(R.id.tool_description) as TextView
	private val toolCheck = itemView.findViewById(R.id.tool_check) as CheckBox
	private val critical = itemView.findViewById(R.id.critical_tool) as View
	private val note = itemView.findViewById(R.id.note_added) as View
	private val exported = itemView.findViewById(R.id.exported) as View

	fun populate(tool: Module)
	{
		var notePrefs = itemView.context.getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
		var checkPrefs = itemView.context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)
		var criticalPrefs = itemView.context.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)

		critical.visibility = if (tool.critical || criticalPrefs.contains(tool.id)) View.VISIBLE else View.GONE

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

		itemView.setOnClickListener { view ->
			//
		}
	}
}
