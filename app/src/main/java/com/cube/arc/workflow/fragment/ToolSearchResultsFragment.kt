package com.cube.arc.workflow.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.workflow.adapter.ToolsAdapter
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.manager.SearchManager
import com.cube.arc.workflow.model.Module
import com.cube.lib.util.bind
import com.cube.lib.util.inflate
import com.cube.lib.util.parent

/**
 * Fragment that displays tool search results
 */
class ToolSearchResultsFragment : Fragment()
{
	private val searchInput by bind<EditText>(R.id.search_input)
	private val resultCount by bind<TextView>(R.id.result_count)
	private val recyclerView by bind<RecyclerView>(R.id.recycler_view)
	private val adapter = ToolsAdapter()
	private var query: String = ""

	companion object
	{
		/**
		 * Creates a new instance of the search results fragment with the given search query
		 */
		fun newInstance(query: String): ToolSearchResultsFragment
		{
			return ToolSearchResultsFragment().apply {
				this.query = query
			}
		}
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = container?.inflate(R.layout.tool_search_results_fragment_view)

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		searchInput.setText(query)
		searchInput.setOnEditorActionListener { view, actionId, event ->
			if (actionId == EditorInfo.IME_ACTION_SEARCH)
			{
				search(view.text.toString())
				true
			}

			false
		}

		ViewCompat.setNestedScrollingEnabled(recyclerView, false);
		recyclerView.layoutManager = LinearLayoutManager(activity)
		recyclerView.adapter = adapter
		search(query)
	}

	/**
	 * Searches the index database for the given query
	 */
	fun search(searchQuery: String)
	{
		this.query = searchQuery
		val searchResults = SearchManager.search(searchQuery)
		val adapterItems = LinkedHashSet<Module>()
		val groupHeaders = LinkedHashSet<String>()

		searchResults.forEach { searchResult ->
			val module = ModulesManager.module(searchResult.moduleId)

			module?.apply {
				// only tools will have null steps
				if (steps == null)
				{
					var parent = parent()

					parent?.let { item ->
						groupHeaders.add(item.id)
						adapterItems.add(item)
					}

					adapterItems.add(this)
				}
			}
		}

		adapter.items = adapterItems.toList()
		adapter.groups = groupHeaders.toList()
		adapter.notifyDataSetChanged()

		resultCount.text = resources.getString(R.string.tool_search_results_count, (adapter.items.size - adapter.groups.size).toString(), searchQuery)

		val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
		imm.hideSoftInputFromWindow(searchInput.windowToken, 0)
	}
}
