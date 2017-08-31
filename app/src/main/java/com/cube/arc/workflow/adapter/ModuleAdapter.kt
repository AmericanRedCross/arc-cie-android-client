package com.cube.arc.workflow.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.cube.arc.R
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.model.Module
import com.cube.arc.workflow.view.holder.ModuleViewHolder
import com.cube.lib.util.inflate

/**
 * Dataset adapter used for rendering modules in a recycler view
 */
class ModuleAdapter : RecyclerView.Adapter<ModuleViewHolder>()
{
	var items : List<Module> = ModulesManager.modules

	override fun onBindViewHolder(holder: ModuleViewHolder?, position: Int)
	{
		holder?.populate(items[position])
	}

	override fun getItemCount(): Int = items.size
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder = ModuleViewHolder(parent.inflate(R.layout.module_item_view))
}
