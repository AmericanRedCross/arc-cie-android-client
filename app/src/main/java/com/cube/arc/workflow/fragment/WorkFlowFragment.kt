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
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.workflow.activity.ToolSearchResultsActivity
import com.cube.arc.workflow.adapter.DirectoryAdapter
import com.cube.arc.workflow.adapter.ToolsAdapter
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.util.bind
import com.cube.lib.util.criticalDirectories
import com.cube.lib.util.inflate

/**
 * Fragment for displaying and handling the workflow feature. Will display a list of directories and its directories/substeps
 */
class WorkFlowFragment : Fragment()
{
	private val recyclerView by bind<RecyclerView>(R.id.recycler_view)
	private val directoriesFilter by bind<RadioButton>(R.id.filter_directories)
	private val criticalFilter by bind<RadioButton>(R.id.filter_critical)
	private val scroller by bind<NestedScrollView>(R.id.scroller)
	private val searchInput by bind<EditText>(R.id.search_input)

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = container?.inflate(R.layout.workflow_fragment_view)

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		AnalyticsHelper.userViewsWorkflow()

		setUi()
		showModules()
	}

	fun setUi()
	{
		recyclerView.layoutManager = LinearLayoutManager(activity)

		ViewCompat.setNestedScrollingEnabled(recyclerView, false);
		ViewCompat.setNestedScrollingEnabled(scroller.getChildAt(0), false);

		directoriesFilter.setOnClickListener {
			AnalyticsHelper.userTapsToolkit()
			showModules()
		}

		criticalFilter.setOnClickListener {
			AnalyticsHelper.userTapsCriticalTools()
			showCritical()
		}

		searchInput.setOnEditorActionListener { view, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH)
			{
				AnalyticsHelper.userSearches(searchInput.text.toString())

				IntentDataHelper.store("search_query", searchInput.text.toString())
				view.context.startActivity(Intent(view.context, ToolSearchResultsActivity::class.java))
				searchInput.setText("")

				true
			}

			false
		}
	}

	override fun onResume()
	{
		super.onResume()
		recyclerView.adapter?.notifyDataSetChanged()
	}

	/**
	 * Shows the directories in the list
	 */
	fun showModules()
	{
		directoriesFilter.isChecked = true
		criticalFilter.isChecked = false
		searchInput.visibility = View.VISIBLE

		recyclerView.adapter = DirectoryAdapter()

		recyclerView.setPadding(0, resources.getDimensionPixelSize(R.dimen.dp8), 0, 0)
		recyclerView.post {
			scroller.scrollTo(0, recyclerView.top + recyclerView.paddingTop)
		}
	}

	/**
	 * Filters and shows critical tools, also includes tools marked as critical by user
	 */
	fun showCritical()
	{
		directoriesFilter.isChecked = false
		criticalFilter.isChecked = true
		searchInput.visibility = View.GONE

		val adapter = ToolsAdapter()
		val items = DirectoryManager.criticalDirectories(true, true, activity)
		val adapterItems = LinkedHashSet<Directory>()
		val groupHeaders = LinkedHashSet<Int>()

		items.forEach { directory ->
			var parent = DirectoryManager.parent(directory)

			parent?.let { item ->
				groupHeaders.add(item.id)
				adapterItems.add(item)
			}

			adapterItems.add(directory)
		}

		adapter.items = adapterItems.toList()
		adapter.groups = groupHeaders.toList()
		recyclerView.adapter = adapter

		recyclerView.setPadding(0, 0, 0, 0)
		recyclerView.post {
			scroller.scrollTo(0, recyclerView.top + recyclerView.paddingTop)
		}
	}
}
