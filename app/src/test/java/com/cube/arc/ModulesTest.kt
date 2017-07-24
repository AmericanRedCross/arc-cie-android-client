package com.cube.arc

import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.manager.flatSteps
import com.cube.arc.workflow.manager.parent
import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 * Basic unit tests for [com.cube.arc.workflow.manager.ModulesManager]
 */
class ModulesTest
{
	@Before fun setUp()
	{
		ModulesManager.init(this::class.java.classLoader.getResourceAsStream("modules.json"))

		assert(ModulesManager.modules.isNotEmpty())
	}

	@Test fun recursiveFlatMap()
	{
		val allModules = ModulesManager.modules.flatSteps()
		Assert.assertEquals(10, allModules.size)
	}

	/**
	 * Tests the [ModulesManager.searchParent] method returns the correct parent object
	 */
	@Test fun getModuleParent()
	{
		// 2 -> 1
		val parent = ModulesManager.searchParent("2")
		Assert.assertNotNull(parent)
		Assert.assertEquals("1", parent?.id)

		// 9 -> 8 -> 5 -> 1
		val parent2 = ModulesManager.searchParent("9")
		Assert.assertNotNull(parent2)
		Assert.assertEquals("8", parent2?.id)

		// 6 -> 5 -> 1
		val module = ModulesManager.module("6")
		Assert.assertNotNull(module)
		val parent3 = module?.parent()
		Assert.assertNotNull(parent3)
		Assert.assertEquals("5", parent3?.id)
	}

	/**
	 * Tests [ModulesManager.modules] method filters and maps correctly
	 */
	@Test fun criticalTools()
	{
		var tools = ModulesManager.modules(true)
		Assert.assertEquals(2, tools.size)
	}

	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun subStepCounter()
	{
		val subStepCount = ModulesManager.subStepCount(ModulesManager.modules[0])
		assert(subStepCount == 3)
	}

	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun toolsCounter()
	{
		val toolsCount = ModulesManager.toolCount(ModulesManager.modules[0])
		assert(toolsCount == 4)
	}

	@Test fun criticalToolsCounter()
	{
		val criticalCount = ModulesManager.toolCount(ModulesManager.modules[0], onlyCritical = true)
		assert(criticalCount == 2)
	}
}
