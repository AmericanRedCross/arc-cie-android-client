package com.cube.arc.progress.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.cube.arc.R
import com.cube.arc.progress.view.holder.ProgressModuleViewHolder
import com.cube.arc.workflow.model.Module
import com.cube.lib.util.inflate

/**
 * Adapter for displaying top level modules and their completion progress
 */
class ProgressModuleAdapter : RecyclerView.Adapter<ProgressModuleViewHolder>()
{
	var items : List<Module> = listOf()

	override fun onBindViewHolder(holder: ProgressModuleViewHolder?, position: Int)
	{
		holder?.populate(items[position])
	}

	override fun getItemCount(): Int = items.size
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressModuleViewHolder = ProgressModuleViewHolder(parent.inflate(R.layout.progress_module_item_view))
}
