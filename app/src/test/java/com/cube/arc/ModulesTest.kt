package com.cube.arc

import com.cube.arc.workflow.manager.ModulesManager
import junit.framework.Assert
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
	
	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun subStepCounter()
	{
		val subStepCount = ModulesManager.subStepCount(ModulesManager.modules[0])
		Assert.assertTrue(subStepCount == 3)
	}

	@Test fun searchModule()
	{
		val thirdDeep = ModulesManager.module("9")
		Assert.assertNotNull(thirdDeep)
	}

	/**
	 * Tests the substep counter calculates substep number correctly
	 */
	@Test fun toolsCounter()
	{
		val toolsCount = ModulesManager.toolCount(ModulesManager.modules[0])
		Assert.assertTrue(toolsCount == 4)
	}

	@Test fun criticalToolsCounter()
	{
		val criticalCount = ModulesManager.toolCount(ModulesManager.modules[0], onlyCritical = true)
		Assert.assertTrue(criticalCount == 2)
	}
}
