package com.cube.lib.util

import org.kamranzafar.jtar.TarInputStream
import java.io.*
import java.util.zip.GZIPInputStream

/**
 * Extracts a provided tar file to the given extraction path.
 *
 * Returns true if extracted successfully or not
 */
fun File.extractTo(extractTo: File): Boolean
{
	try
	{
		val buffer = 8192
		var totalRead: Long = 0

		val stream = BufferedInputStream(GZIPInputStream(FileInputStream(this), buffer), buffer)
		val tis = TarInputStream(stream)

		while (true)
		{
			val file = tis.nextEntry ?: break
			if (file.name == "./") continue

			val extractedFilePath = extractTo.absolutePath + "/" + file.name
			val extractFile = File(extractedFilePath)

			if (file.isDirectory)
			{
				extractFile.mkdirs()
				continue
			}

			// create folders if they do not exist for file
			if (!File(extractFile.parent).exists())
			{
				File(extractFile.parent).mkdirs()
			}

			val fos = FileOutputStream(extractedFilePath)
			val dest = BufferedOutputStream(fos, buffer)

			var count = 0
			val data = ByteArray(buffer)

			while (true)
			{
				count = tis.read(data)

				if (count == -1) break

				dest.write(data, 0, count)
				totalRead += count.toLong()
			}

			dest.flush()
			dest.close()
		}

		tis.close()
	}
	catch (e: IOException)
	{
		e.printStackTrace()
		return false
	}

	return true
}
