package com.cube.arc.cie.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.cube.arc.R

/**
 * Main activity host for the workflow and progress tabs
 */
class MainActivity : AppCompatActivity()
{
	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.main_activity_view)
	}
}
