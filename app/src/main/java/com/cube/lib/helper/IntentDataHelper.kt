package com.cube.lib.helper

import java.util.*

/**
 * Singleton class used for passing large data sets around without having to serialise.
 * This class should **not** reference any kind of context without implicitly destroying
 * the object when it is done to prevent Context leaks. Be wary!
 *
 * @author Callum Taylor
 */
object IntentDataHelper
{
	private var dataStore = WeakHashMap<String, Any>()

	/**
	 * Stores object data against a class name. Operation is destructive, if {@param tag} already exists in the data store map.
	 *
	 * @param tag The tag to store against, use the same tag name to retrieve the data back.
	 * @param data The data to temporarily store.
	 */
	fun store(tag: Class<*>, data: Any?)
	{
		store(tag.name, data)
	}

	/**
	 * Stores object data against a class name. Operation is destructive, if {@param tag} already exists in the data store map.
	 *
	 * @param tag The tag to store against, use the same tag name to retrieve the data back.
	 * @param data The data to temporarily store.
	 */
	fun store(tag: String, data: Any?)
	{
		dataStore.put(tag, data)
	}

	/**
	 * Gets the stored data object from a given tag, or null if nothing was stored. Operation is destructive,
	 * object will be removed from data store once retrieved.
     *
	 * @param tag The tag used to originally store the data
	 * @return The found object or null.
	 */
	fun retrieve(tag: Class<*>): Any?
	{
		return retrieve(tag.name, Any::class.java)
	}

	/**
	 * Gets the stored data object from a given tag, or null if nothing was stored. Operation is destructive,
	 * object will be removed from data store once retrieved.
     *
	 * @param tag The tag used to originally store the data
	 * @return The found object or null.
	 */
	fun retrieve(tag: String): Any?
	{
		return retrieve(tag, Any::class.java)
	}

	/**
	 * Gets the stored data object from a given tag, or null if nothing was stored. Operation is destructive,
	 * object will be removed from data store once retrieved.
     *
	 * @param tag The tag used to originally store the data
	 * @param classType The class type to force cast to
	 * @return The found object or null.
	 */
	fun <T> retrieve(tag: Class<*>, classType: Class<T>): T?
	{
		return classType.cast(dataStore.remove(tag.name))
	}

	/**
	 * Gets the stored data object from a given tag, or null if nothing was stored. Operation is destructive,
	 * object will be removed from data store once retrieved.
     *
	 * @param tag The tag used to originally store the data
	 * @param classType The class type to force cast to
	 * @return The found object or null.
	 */
	fun <T> retrieve(tag: String, classType: Class<T>): T?
	{
		return classType.cast(dataStore.remove(tag))
	}

	/**
	 * Force destroys the data store to remove all references
	 */
	fun clear()
	{
		dataStore = WeakHashMap<String, Any>()
	}
}
