package com.cube.arc

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.cube.arc.cie.MainApplication
import com.cube.arc.workflow.manager.ExportManager
import com.cube.arc.workflow.manager.DirectoriesManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Basic unit tests for [com.cube.arc.workflow.manager.ExportManager]
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class ExportTest
{
	val appContext = InstrumentationRegistry.getTargetContext()

	@Before fun setUp()
	{
		DirectoriesManager.init(appContext.assets.open("structure.json"))
		MainApplication.BASE_PATH = File("/")

		Assert.assertTrue(DirectoriesManager.directories.isNotEmpty())
	}

	@Test fun csvExport()
	{
		val exported = ExportManager.generateUserContent(appContext)
		Assert.assertNotEquals("", exported)
	}
}
