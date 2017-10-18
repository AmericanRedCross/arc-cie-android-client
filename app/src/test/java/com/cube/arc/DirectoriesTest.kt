package com.cube.arc

import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.flat
import com.cube.lib.util.criticalDirectories
import com.cube.lib.util.subStepCount
import com.cube.lib.util.toolCount
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
		DirectoryManager.init(this::class.java.classLoader.getResourceAsStream("structure.json"))

		assert(DirectoryManager.directories.isNotEmpty())
	}

	@Test fun recursiveFlatMap()
	{
		val allDirectories = DirectoryManager.directories.flat()
		Assert.assertEquals(14, allDirectories.size)
	}

	/**
	 * Tests the [DirectoriesManager.searchParent] method returns the correct parent object
	 */
	@Test fun getDirectoryParent()
	{
		// 20 -> 2
		var root = DirectoryManager.directory(20)
		Assert.assertNotNull(root)

		val parent = DirectoryManager.parent(root!!)
		Assert.assertNotNull(parent)
		Assert.assertEquals(2, parent?.id)

		// 1000 -> 100 -> 10 -> 1
		root = DirectoryManager.directory(1000)
		Assert.assertNotNull(root)

		val parent2 = DirectoryManager.parent(root!!)
		Assert.assertNotNull(parent2)
		Assert.assertEquals(100, parent2?.id)
	}

	/**
	 * Tests [DirectoriesManager.directories] method filters and maps correctly
	 */
	@Test fun criticalTools()
	{
		var tools = DirectoryManager.criticalDirectories(true)
		Assert.assertEquals(2, tools.size)
	}

	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun subStepCounter()
	{
		val subStepCount = DirectoryManager.subStepCount(DirectoryManager.directories[0])
		Assert.assertTrue(subStepCount == 2)
	}

	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun criticalToolsCounter()
	{
		var toolsCount = DirectoryManager.toolCount(DirectoryManager.directories[0], onlyCritical = true)
		Assert.assertTrue(toolsCount == 0)

		toolsCount = DirectoryManager.toolCount(DirectoryManager.directories[1], onlyCritical = true)
		Assert.assertTrue(toolsCount == 2)
	}

	@Test fun toolsCounter()
	{
		val toolsCount = DirectoryManager.toolCount(DirectoryManager.directories[0])
		Assert.assertTrue(toolsCount == 2)
	}
}
