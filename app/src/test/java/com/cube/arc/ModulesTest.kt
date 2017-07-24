package com.cube.arc

import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.manager.flatSteps
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
