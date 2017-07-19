package com.cube.arc.cie.activity

import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import com.cube.arc.R
import com.cube.arc.workflow.fragment.WorkFlowFragment
import com.cube.lib.util.bind

/**
 * Main activity host for the workflow and progress tabs
 */
class MainActivity : AppCompatActivity()
{
	private val bottomNavigation by bind<BottomNavigationView>(R.id.bottom_navigation)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main_activity_view)
		with (findViewById(R.id.toolbar) as Toolbar)
		{
			setSupportActionBar(this)
			title = getString(R.string.app_name)
		}

		if (savedInstanceState == null)
		{
			setUi()
		}
	}

	fun setUi()
	{
		bottomNavigation.setOnNavigationItemSelectedListener { item ->
			var fragment = when (item.itemId)
			{
				 R.id.menu_workflow -> WorkFlowFragment()
				 R.id.menu_progress -> Fragment()
				else -> Fragment()
			}

			supportFragmentManager.beginTransaction()
				.replace(R.id.fragment_holder, fragment)
				.commit()

			true
		}
	}
}
