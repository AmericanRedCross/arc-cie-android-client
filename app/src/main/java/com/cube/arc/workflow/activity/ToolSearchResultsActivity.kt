package com.cube.arc.workflow.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.cube.arc.R
import com.cube.arc.workflow.fragment.ToolSearchResultsFragment
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.helper.IntentDataHelper
import kotlinx.android.synthetic.main.fragment_activity_view.*

/**
 * Host activity for search results fragment
 */
class ToolSearchResultsActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		AnalyticsHelper.userViewsSearchResults()

		setContentView(R.layout.fragment_activity_view)
		setSupportActionBar(toolbar)

		if (savedInstanceState == null)
		{
			supportFragmentManager.beginTransaction()
				.replace(R.id.fragment_holder, ToolSearchResultsFragment.newInstance(IntentDataHelper.retrieve("search_query")))
				.commit()
		}
	}
}
