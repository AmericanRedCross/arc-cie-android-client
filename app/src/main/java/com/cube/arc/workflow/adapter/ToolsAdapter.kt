package com.cube.arc.workflow.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.cube.arc.R
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.workflow.view.holder.ToolDividerViewHolder
import com.cube.arc.workflow.view.holder.ToolViewHolder
import com.cube.lib.util.inflate

/**
 * Dataset adapter used for rendering document tools in a recycler view
 */
class ToolsAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
	private val TYPE_DIVIDER = 0
	private val TYPE_TOOL = 1

	var items: List<Directory> = listOf()

	/**
	 * List of item groups, will be returned before
	 */
	var groups: List<Int> = listOf()

	override fun getItemViewType(position: Int): Int = when
	{
		groups.contains(items[position].id) -> TYPE_DIVIDER
		else -> TYPE_TOOL
	}

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int)
	{
		when (holder)
		{
			is ToolViewHolder -> holder?.populate(null, items[position])
			is ToolDividerViewHolder -> holder?.populate(items[position])
		}
	}

	override fun getItemCount(): Int = items.size

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder
	{
		return when (viewType)
		{
			TYPE_DIVIDER -> ToolDividerViewHolder(parent.inflate(R.layout.tool_item_view))
			else -> ToolViewHolder(parent.inflate(R.layout.substep_tool_stub))
		}
	}
}
