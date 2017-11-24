package com.cube.arc.workflow.manager

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.workflow.model.SearchResult
import java.util.*

/**
 * Manager classed used for full text searching through storm language content and matching to page ID
 *
 * @author Callum Taylor
 */
object SearchManager
{
	@SuppressLint("StaticFieldLeak")
	private lateinit var sqliteHelper: SQLiteHelper

	/**
	 * @param context The application context used for loading the database
	 */
	fun init(context: Context)
	{
		sqliteHelper = SQLiteHelper(context.applicationContext)

		Thread(Runnable {
			index()
		}).start()
	}

	/**
	 * This method should not be called on the UI thread.
	 */
	@Synchronized fun index()
	{
		val database = sqliteHelper.writableDatabase

		run {
			// index
			indexFiles(database)
			database.close()
		}
	}

	/**
	 * Searches the indexed database for the given query.
	 *
	 * @param query The query to search
	 *
	 * @return The list of results, or an empty list
	 */
	fun search(query: String): List<SearchResult>
	{
		val results = ArrayList<SearchResult>()
		synchronized (this)
		{
			val database = sqliteHelper.readableDatabase

			val sql = "SELECT * FROM search WHERE id IN (SELECT docid FROM search_index WHERE search_index MATCH ?)"
			val selectionArgs = arrayOf(query + "*")
			val cursor = database.rawQuery(sql, selectionArgs)

			while (cursor.moveToNext())
			{
				val title = cursor.getString(cursor.getColumnIndex("title")) ?: ""
				val directoryId = cursor.getInt(cursor.getColumnIndex("directory_id"))

				val result = SearchResult(query, directoryId, title)
				results.add(result)
			}

			cursor.close()
		}

		return results
	}

	/**
	 * @param database The database instance to operate on
	 */
	fun indexFiles(database: SQLiteDatabase)
	{
		database.execSQL("DELETE FROM search;")
		database.execSQL("DELETE FROM search_index;")
		database.beginTransaction()

		DirectoryManager.directories.forEach { directories ->
			directories.directories.forEach { step ->
				step.directories.forEach { subStep ->
					subStep.directories.forEach { tool ->
						val values = ContentValues()
						values.put("title", tool.title)
						values.put("directory_id", tool.id)

						database.insert("search", null, values)
					}
				}
			}
		}

		// perform virtual table index
		database.execSQL("INSERT INTO search_index (docid, title) SELECT id, title FROM search;")

		database.setTransactionSuccessful()
		database.endTransaction()
	}

	class SQLiteHelper(private val context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
	{
		companion object
		{
			private val DATABASE_NAME = "search.db"
			private val DATABASE_VERSION = 1
		}

		override fun onCreate(db: SQLiteDatabase)
		{
			try
			{
				val commands = arrayOf(
					"CREATE TABLE search (id INTEGER PRIMARY KEY, title TEXT, directory_id INTEGER);",
					"CREATE VIRTUAL TABLE search_index USING fts4 (content='search', title);",
					"CREATE TABLE meta (id INTEGER PRIMARY KEY, last_update NUMERIC);",
					"INSERT INTO meta VALUES (1, 0);"
				)

				for (command in commands)
				{
					db.execSQL(command)
				}
			}
			catch (e: Exception)
			{
				e.printStackTrace()
			}
		}

		override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
		{
		}
	}
}
