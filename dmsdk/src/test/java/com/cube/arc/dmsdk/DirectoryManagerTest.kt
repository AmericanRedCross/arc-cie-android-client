package com.cube.arc.dmsdk

import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.google.gson.JsonParser
import junit.framework.Assert
import org.junit.Before
import org.junit.Test

class DirectoryManagerTest
{
	@Before fun setUp()
	{
		// test init overload
		test_init_from_string()
		test_init_from_json()
		test_init_from_object()

		// load test data from resources
		test_init_from_stream()
	}

	fun test_init_from_stream()
	{
		DirectoryManager.init(this::class.java.classLoader.getResourceAsStream("structure.json"))

		Assert.assertTrue(DirectoryManager.directories.isNotEmpty())
	}

	fun test_init_from_json()
	{
		val jsonString = this::class.java.classLoader.getResourceAsStream("structure.json").bufferedReader().use { it.readText() }
		val jsonElement = JsonParser().parse(jsonString)
		DirectoryManager.init(jsonElement)

		Assert.assertTrue(DirectoryManager.directories.isNotEmpty())
	}

	fun test_init_from_string()
	{
		val jsonString = this::class.java.classLoader.getResourceAsStream("structure.json").bufferedReader().use { it.readText() }
		DirectoryManager.init(jsonString)

		Assert.assertTrue(DirectoryManager.directories.isNotEmpty())
	}

	fun test_init_from_object()
	{
		val directories = arrayListOf(
			Directory(
				id = 1,
				order = 0,
				title = "Directory 1",
				directories = arrayListOf(
					Directory(
						id = 10,
						order = 0,
						title = "Directory 10"
					)
				)
			),
			Directory(
				id = 2,
				order = 1,
				title = "Directory 2"
			)
		)

		DirectoryManager.init(directories)

		Assert.assertTrue(DirectoryManager.directories.isNotEmpty())
	}

	@Test
	fun test_search_existing_id()
	{
		val result = DirectoryManager.directory(1)
		Assert.assertNotNull(result)
	}

	@Test
	fun test_search_nonexisting_id()
	{
		val result = DirectoryManager.directory(Int.MAX_VALUE)
		Assert.assertNull(result)
	}

	@Test
	fun test_directory_parent()
	{
		val directory = DirectoryManager.directory(2000)
		Assert.assertNotNull(directory)

		val parent = DirectoryManager.directory(directory?.parentId ?: 0)
		Assert.assertNotNull(parent)

		val foundParent = DirectoryManager.parent(directory!!)

		Assert.assertNotNull(foundParent)
		Assert.assertEquals(parent, foundParent)
	}
}
