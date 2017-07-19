package com.cube.arc.workflow.fragment

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cube.arc.R
import com.cube.arc.workflow.adapter.ModuleAdapter
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.lib.util.bind

/**
 * Fragment for displaying and handling the workflow feature. Will display a list of modules and its steps/substeps
 */
class WorkFlowFragment : Fragment()
{
	val recyclerView by bind<RecyclerView>(R.id.recycler_view)
	val adapter = ModuleAdapter()

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View?
	{
		return inflater?.inflate(R.layout.workflow_fragment_view, container, false)
	}

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		if (savedInstanceState == null)
		{
			adapter.items = ModulesManager.modules

			val layoutManager = LinearLayoutManager(activity)
			val itemDecoration = object : RecyclerView.ItemDecoration()
			{
				override fun getItemOffsets(outRect: Rect?, view: View?, parent: RecyclerView?, state: RecyclerView.State?)
				{
					val itemPosition = parent?.getChildAdapterPosition(view) ?: 0

					if (itemPosition > 0)
					{
//						outRect?.set(0, -resources.getDimensionPixelSize(R.dimen.workflow_card_offset), 0, 0)
					}
				}
			}

			recyclerView.addItemDecoration(itemDecoration)
			recyclerView.layoutManager = layoutManager
			recyclerView.adapter = adapter
		}
	}
}
