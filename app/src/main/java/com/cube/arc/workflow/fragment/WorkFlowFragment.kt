package com.cube.arc.workflow.fragment

import android.graphics.Rect
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import com.cube.arc.R
import com.cube.arc.workflow.adapter.ModuleAdapter
import com.cube.arc.workflow.adapter.ToolsAdapter
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.model.Module
import com.cube.lib.util.bind
import com.cube.lib.util.inflate
import com.cube.lib.util.parent

/**
 * Fragment for displaying and handling the workflow feature. Will display a list of modules and its steps/substeps
 */
class WorkFlowFragment : Fragment()
{
	private val recyclerView by bind<RecyclerView>(R.id.recycler_view)
	private val modulesFilter by bind<RadioButton>(R.id.filter_modules)
	private val criticalFilter by bind<RadioButton>(R.id.filter_critical)

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = container?.inflate(R.layout.workflow_fragment_view)

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		if (savedInstanceState == null)
		{
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

			val adapter = ModuleAdapter()
			adapter.items = ModulesManager.modules

			recyclerView.addItemDecoration(itemDecoration)
			recyclerView.layoutManager = layoutManager
			recyclerView.adapter = adapter;
		}

		modulesFilter.setOnClickListener { view ->
			criticalFilter.isChecked = false

			val adapter = ModuleAdapter()
			adapter.items = ModulesManager.modules
			recyclerView.adapter = adapter
		}

		criticalFilter.setOnClickListener { view ->
			modulesFilter.isChecked = false

			val adapter = ToolsAdapter()
			val items = ModulesManager.modules(true, true, view.context)
			val adapterItems = LinkedHashSet<Module>()
			val groupHeaders = LinkedHashSet<String>()

			items.forEach { module ->
				var parent = module.parent()

				parent?.let { item ->
					groupHeaders.add(item.id)
					adapterItems.add(item)
				}

				adapterItems.add(module)
			}

			adapter.items = adapterItems.toList()
			adapter.groups = groupHeaders.toList()
			recyclerView.adapter = adapter
		}
	}
}
