package com.cube.arc.workflow.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.cube.arc.R
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.workflow.view.holder.DirectoryViewHolder
import com.cube.lib.util.inflate

/**
 * Dataset adapter used for rendering directories in a recycler view
 */
class DirectoryAdapter : RecyclerView.Adapter<DirectoryViewHolder>()
{
	var items : List<Directory> = DirectoryManager.directories

	override fun onBindViewHolder(holder: DirectoryViewHolder?, position: Int)
	{
		holder?.populate(items[position])
	}

	override fun getItemCount(): Int = items.size
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirectoryViewHolder = DirectoryViewHolder(parent.inflate(R.layout.directory_item_view))
}
