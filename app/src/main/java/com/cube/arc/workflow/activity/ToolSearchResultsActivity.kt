package com.cube.arc.workflow.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.cube.arc.R
import com.cube.arc.workflow.fragment.ToolSearchResultsFragment
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.helper.IntentDataHelper

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
		setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

		if (savedInstanceState == null)
		{
			supportFragmentManager.beginTransaction()
				.replace(R.id.fragment_holder, ToolSearchResultsFragment.newInstance(IntentDataHelper.retrieve("search_query")))
				.commit()
		}
	}
}
