package com.cube.arc

import android.support.test.InstrumentationRegistry
import android.support.test.filters.SmallTest
import android.support.test.runner.AndroidJUnit4
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.manager.SearchManager
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Tests [SearchManaager] methods
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class SearchManagerTest
{
	val appContext = InstrumentationRegistry.getTargetContext()

	@Before
	fun setup()
	{
		ModulesManager.init(appContext.assets.open("modules.json"))
		SearchManager.init(appContext)
	}

	@Test
	fun testSearch()
	{
		val results = SearchManager.search("step")
		Assert.assertEquals(23, results.size)
	}
}
