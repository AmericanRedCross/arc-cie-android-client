package com.cube.arc

import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite to run all tests in the test package
 */
@RunWith(Suite::class)
@Suite.SuiteClasses(
	ExportTest::class,
	SearchManagerTest::class
)
class AndroidTestSuite
