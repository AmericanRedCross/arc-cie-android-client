package com.cube.arc.workflow.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cube.arc.R
import com.cube.arc.workflow.model.Module
import com.cube.arc.workflow.view.holder.ModuleViewHolder

/**
 * Dataset adapter used for rendering modules in a recycler view
 */
class ModuleAdapter : RecyclerView.Adapter<ModuleViewHolder>()
{
	var items : List<Module> = listOf<Module>()

	override fun onBindViewHolder(holder: ModuleViewHolder?, position: Int)
	{

	}

	override fun getItemCount(): Int
	{
		return items.size
	}

	override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ModuleViewHolder
	{
		return ModuleViewHolder(LayoutInflater.from(parent?.context).inflate(R.layout.module_item_view, parent, false))
	}
}
