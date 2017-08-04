package com.cube.arc.cie

import android.app.Application
import android.os.Environment
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.manager.SearchManager
import java.io.File

/**
 * Application singleton for instantiating application configuration and data files
 */
class MainApplication : Application()
{
	companion object
	{
		public lateinit var BASE_PATH: File
	}

	override fun onCreate()
	{
		super.onCreate()

		// initialise module manager
		BASE_PATH = File(Environment.getExternalStorageDirectory().absoluteFile, "CIE-Documents")
		ModulesManager.init(resources.assets.open("modules.json"))
		SearchManager.init(this)
	}
}
