package com.cube.arc.cie

import android.app.Application
import com.cube.arc.workflow.manager.ModulesManager

/**
 * Application singleton for instantiating application configuration and data files
 */
class MainApplication : Application()
{
	override fun onCreate()
	{
		super.onCreate()

		// initialise module manager
		ModulesManager.init(resources.assets.open("modules.json"))
	}
}
