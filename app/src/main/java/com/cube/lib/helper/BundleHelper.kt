package com.cube.lib.helper

import android.content.Context
import java.io.File
import java.io.FileInputStream
import java.io.InputStream

/**
 * Helper class used to decide to look on disk or assets for a given file
 *
 * @author Callum Taylor
 */
class BundleHelper
{
	companion object
	{
		/**
		 * Resolves a file to an inputstream either from disk or from bundled assets
		 */
		fun resolve(filePath: String, context: Context): InputStream?
		{
			val absFilePath = if (filePath.startsWith("/")) filePath.substring(1) else filePath
			val cachePath = File(context.filesDir, absFilePath)

			if (cachePath.exists())
			{
				return FileInputStream(cachePath)
			}
			else
			{
				try
				{
					return context.assets.open(absFilePath)
				}
				catch (e: Exception)
				{
					return null
				}
			}
		}
	}
}
