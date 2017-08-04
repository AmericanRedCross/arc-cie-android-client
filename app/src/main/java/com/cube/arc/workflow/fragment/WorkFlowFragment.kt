package com.cube.arc.workflow.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RadioButton
import com.cube.arc.R
import com.cube.arc.workflow.activity.ToolSearchResultsActivity
import com.cube.arc.workflow.adapter.ModuleAdapter
import com.cube.arc.workflow.adapter.ToolsAdapter
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.model.Module
import com.cube.lib.helper.IntentDataHelper
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
	private val scroller by bind<NestedScrollView>(R.id.scroller)
	private val searchInput by bind<EditText>(R.id.search_input)

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = container?.inflate(R.layout.workflow_fragment_view)

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		setUi()
		showModules()
	}

	fun setUi()
	{
		recyclerView.layoutManager = LinearLayoutManager(activity)

		ViewCompat.setNestedScrollingEnabled(recyclerView, false);

		modulesFilter.setOnClickListener {
			showModules()
		}

		criticalFilter.setOnClickListener {
			showCritical()
		}

		searchInput.setOnEditorActionListener { view, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH)
			{
				IntentDataHelper.store("search_query", searchInput.text.toString())
				view.context.startActivity(Intent(view.context, ToolSearchResultsActivity::class.java))
				true
			}

			false
		}
	}

	/**
	 * Shows the modules in the list
	 */
	fun showModules()
	{
		modulesFilter.isChecked = true
		criticalFilter.isChecked = false
		searchInput.visibility = View.VISIBLE

		val adapter = ModuleAdapter()
		adapter.items = ModulesManager.modules
		recyclerView.adapter = adapter

		recyclerView.post {
			scroller.scrollTo(0, recyclerView.top)
		}
	}

	/**
	 * Filters and shows critical tools, also includes tools marked as critical by user
	 */
	fun showCritical()
	{
		modulesFilter.isChecked = false
		criticalFilter.isChecked = true
		searchInput.visibility = View.GONE

		val adapter = ToolsAdapter()
		val items = ModulesManager.modules(true, true, activity)
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
