package com.cube.arc

import com.cube.arc.workflow.manager.ModulesManager
import com.cube.lib.util.flatSteps
import com.cube.lib.util.parent
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
		ModulesManager.init(this::class.java.classLoader.getResourceAsStream("structure.json"))

		assert(ModulesManager.modules.isNotEmpty())
	}

	@Test fun recursiveFlatMap()
	{
		val allModules = ModulesManager.modules.flatSteps()
		Assert.assertEquals(10, allModules.size)
	}

	@Test fun modulesDepth()
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

		Assert.assertEquals(0, ModulesManager.tree["1"])
		Assert.assertEquals(1, ModulesManager.tree["2"])
		Assert.assertEquals(2, ModulesManager.tree["3"])
		Assert.assertEquals(3, ModulesManager.tree["4"])
		Assert.assertEquals(1, ModulesManager.tree["5"])
		Assert.assertEquals(2, ModulesManager.tree["6"])
		Assert.assertEquals(3, ModulesManager.tree["7"])
		Assert.assertEquals(2, ModulesManager.tree["8"])
		Assert.assertEquals(3, ModulesManager.tree["9"])
		Assert.assertEquals(3, ModulesManager.tree["10"])
	}

	/**
	 * Ensures the index of each item in the tree is the same as in the flat map of items
	 */
	@Test fun modulesDepthIndex()
	{
		val allSteps = ModulesManager.modules.flatSteps()
		val keys = ModulesManager.tree.keys

		allSteps.forEachIndexed { index, module ->
			Assert.assertEquals(index, keys.indexOf(module.id))
		}
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
