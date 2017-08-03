package com.cube.arc.cie

import android.app.Application
import com.cube.arc.workflow.manager.ModulesManager
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
		ModulesManager.init(resources.assets.open("modules.json"))
		BASE_PATH = File(getExternalFilesDir(null), "CIE-Documents")
	}
}
