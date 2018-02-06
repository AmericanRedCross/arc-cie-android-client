package com.cube.arc

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.cube.arc.cie.MainApplication
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.workflow.manager.SearchManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

/**
 * Tests [SearchManaager] methods
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class SearchManagerTest
{
	val testContext = InstrumentationRegistry.getContext()
	val appContext = InstrumentationRegistry.getTargetContext()

	@Before fun setUp()
	{
		DirectoryManager.init(testContext.assets.open("structure.json"))
		MainApplication.BASE_PATH = File("/")

		Assert.assertTrue(DirectoryManager.directories.isNotEmpty())
		SearchManager.init(appContext)
	}

	@Test fun testSearch()
	{
		val results = SearchManager.search("ID: 1")
		Assert.assertEquals(2, results.size)
	}
}
