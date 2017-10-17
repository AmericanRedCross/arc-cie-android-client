package com.cube.arc.progress.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.cube.arc.R
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.progress.view.holder.ProgressDirectoryViewHolder
import com.cube.lib.util.inflate

/**
 * Adapter for displaying top level directories and their completion progress
 */
class ProgressDirectoryAdapter : RecyclerView.Adapter<ProgressDirectoryViewHolder>()
{
	var items: List<Directory> = listOf()

	override fun onBindViewHolder(holder: ProgressDirectoryViewHolder?, position: Int)
	{
		holder?.populate(items[position])
	}

	override fun getItemCount(): Int = items.size
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProgressDirectoryViewHolder = ProgressDirectoryViewHolder(parent.inflate(R.layout.progress_directory_item_view))
}
