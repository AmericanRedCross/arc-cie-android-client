package com.cube.arc.cie.activity

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.cube.arc.R
import com.cube.arc.progress.fragment.ProgressFragment
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

	override fun onCreateOptionsMenu(menu: Menu?): Boolean
	{
		menuInflater.inflate(R.menu.main_menu, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem?): Boolean
	{
		when (item?.itemId)
		{
			R.id.menu_settings -> startActivity(Intent(this, SettingsActivity::class.java))
			R.id.menu_export -> {
				startActivity(Intent(this, ExportActivity::class.java))
			}
			else -> return false
		}

		return true
	}

	fun setUi()
	{
		val select = ({ itemId: Int ->
			supportFragmentManager.beginTransaction()
				.replace(R.id.fragment_holder, when (itemId)
					{
						R.id.menu_workflow -> WorkFlowFragment()
						R.id.menu_progress -> ProgressFragment()
						else -> Fragment()
					})
				.commit()
		})

		bottomNavigation.setOnNavigationItemSelectedListener { item ->
			if (bottomNavigation.selectedItemId != item.itemId)
			{
				select.invoke(item.itemId)
			}

			true
		}

		select.invoke(R.id.menu_workflow)
	}
}
