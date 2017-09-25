package com.cube.arc.cie

import android.app.Application
import android.os.Environment
import com.cube.arc.workflow.manager.DirectoriesManager
import com.cube.arc.workflow.manager.SearchManager
import com.cube.lib.helper.AnalyticsHelper
import java.io.File
import java.io.FileInputStream

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

		AnalyticsHelper.initialise(this)

		// initialise directory manager
		BASE_PATH = File(Environment.getExternalStorageDirectory().absoluteFile, "CIE-Documents")
		initManagers()
	}

	/**
	 * Initialises the manager classes
	 */
	fun initManagers()
	{
		val cacheModules = File(filesDir, "structure.json")
		if (cacheModules.exists())
		{
			DirectoriesManager.init(FileInputStream(cacheModules))
			SearchManager.init(this)
		}
	}
}
