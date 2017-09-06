package com.cube.arc

import com.cube.arc.workflow.manager.DirectoriesManager
import com.cube.lib.util.flatSteps
import com.cube.lib.util.parent
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Basic unit tests for [com.cube.arc.workflow.manager.DirectoriesManager]
 */
class DirectoriesTest
{
	@Before fun setUp()
	{
		DirectoriesManager.init(this::class.java.classLoader.getResourceAsStream("structure.json"))

		assert(DirectoriesManager.directories.isNotEmpty())
	}

	@Test fun recursiveFlatMap()
	{
		val allDirectories = DirectoriesManager.directories.flatSteps()
		Assert.assertEquals(10, allDirectories.size)
	}

	@Test fun directoriesDepth()
	{
		/**
		 * 	1
		 * 		2
		 * 			3
		 * 				4
		 * 		5
		 * 			6
		 * 				7
		 * 			8
		 * 				9
		 * 				10
		 */

		Assert.assertEquals(0, DirectoriesManager.tree["1"])
		Assert.assertEquals(1, DirectoriesManager.tree["2"])
		Assert.assertEquals(2, DirectoriesManager.tree["3"])
		Assert.assertEquals(3, DirectoriesManager.tree["4"])
		Assert.assertEquals(1, DirectoriesManager.tree["5"])
		Assert.assertEquals(2, DirectoriesManager.tree["6"])
		Assert.assertEquals(3, DirectoriesManager.tree["7"])
		Assert.assertEquals(2, DirectoriesManager.tree["8"])
		Assert.assertEquals(3, DirectoriesManager.tree["9"])
		Assert.assertEquals(3, DirectoriesManager.tree["10"])
	}

	/**
	 * Ensures the index of each item in the tree is the same as in the flat map of items
	 */
	@Test fun directoriesDepthIndex()
	{
		val allSteps = DirectoriesManager.directories.flatSteps()
		val keys = DirectoriesManager.tree.keys

		allSteps.forEachIndexed { index, directory ->
			Assert.assertEquals(index, keys.indexOf(directory.id))
		}
	}

	/**
	 * Tests the [DirectoriesManager.searchParent] method returns the correct parent object
	 */
	@Test fun getDirectoryParent()
	{
		// 2 -> 1
		val parent = DirectoriesManager.searchParent("2")
		Assert.assertNotNull(parent)
		Assert.assertEquals("1", parent?.id)

		// 9 -> 8 -> 5 -> 1
		val parent2 = DirectoriesManager.searchParent("9")
		Assert.assertNotNull(parent2)
		Assert.assertEquals("8", parent2?.id)

		// 6 -> 5 -> 1
		val directory = DirectoriesManager.directory("6")
		Assert.assertNotNull(directory)
		val parent3 = directory?.parent()
		Assert.assertNotNull(parent3)
		Assert.assertEquals("5", parent3?.id)
	}

	/**
	 * Tests [DirectoriesManager.directories] method filters and maps correctly
	 */
	@Test fun criticalTools()
	{
		var tools = DirectoriesManager.directories(true)
		Assert.assertEquals(2, tools.size)
	}

	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun subStepCounter()
	{
		val subStepCount = DirectoriesManager.subStepCount(DirectoriesManager.directories[0])
		Assert.assertTrue(subStepCount == 3)
	}

	@Test fun searchDirectory()
	{
		val thirdDeep = DirectoriesManager.directory("9")
		Assert.assertNotNull(thirdDeep)
	}

	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun toolsCounter()
	{
		val toolsCount = DirectoriesManager.toolCount(DirectoriesManager.directories[0])
		Assert.assertTrue(toolsCount == 4)
	}

	@Test fun criticalToolsCounter()
	{
		val criticalCount = DirectoriesManager.toolCount(DirectoriesManager.directories[0], onlyCritical = true)
		Assert.assertTrue(criticalCount == 2)
	}
}
