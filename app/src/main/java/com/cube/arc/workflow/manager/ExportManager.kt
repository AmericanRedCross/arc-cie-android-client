package com.cube.arc.workflow.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.content.FileProvider
import com.cube.arc.BuildConfig
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.dmsdk.model.FileDescriptor
import com.cube.arc.workflow.model.Registry
import com.cube.lib.util.escapeCsv
import com.cube.lib.util.times
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*

/**
 * Manager class used for downloading/opening files associated with directories and tools
 */
object ExportManager
{
	const val REGISTRY = "registry.json"

	init
	{
		MainApplication.BASE_PATH.mkdirs()
	}

	/**
	 * Checks if a file has already been downloaded with a given hash
	 */
	fun isFileDownloaded(file: FileDescriptor): Boolean
	{
		if (!File(MainApplication.BASE_PATH, REGISTRY).exists() || File(MainApplication.BASE_PATH, REGISTRY).length() <= 0) return false

		val registry = Gson().fromJson<ArrayList<Registry>>(FileReader(File(MainApplication.BASE_PATH, REGISTRY)), object : TypeToken<List<Registry>>(){}.type) ?: listOf<Registry>()
		return registry.filter {
			(it.fileName == file.title || it.fileName == file.titleWithExtension) && File(MainApplication.BASE_PATH, it.fileName).exists()
		}.isNotEmpty()
	}

	/**
	 * Registers a downloaded file in a manifest used to calculate if the file is the newest version of
	 * a given [FileDescriptor]
	 */
	fun registerFileManifest(file: FileDescriptor)
	{
		var registry = arrayListOf<Registry>()

		if (File(MainApplication.BASE_PATH, REGISTRY).exists() && File(MainApplication.BASE_PATH, REGISTRY).length() > 0)
		{
			registry = Gson().fromJson<ArrayList<Registry>>(FileReader(File(MainApplication.BASE_PATH, REGISTRY)), object : TypeToken<ArrayList<Registry>>(){}.type) ?: arrayListOf<Registry>()
		}

		val fileRegistry = Registry(file.titleWithExtension, 0)//file.timestamp)
		registry.add(fileRegistry)

		File(MainApplication.BASE_PATH, REGISTRY).bufferedWriter().use { out -> out.write(Gson().toJson(registry).toString()); out.flush() }
	}

	/**
	 * Launches google play with specific app query for a given mime type
	 */
	fun launchStoreForMime(mimeType: String, context: Context)
	{
		val appId = when (mimeType.toLowerCase())
		{
			"application/pdf" -> "com.google.android.apps.pdfviewer"

			"text/plain", // .txt
			"text/richtext", // .rtf
			"application/vnd.oasis.opendocument.text", // .odt
			"application/msword", // .doc
			"application/vnd.openxmlformats-officedocument.wordprocessingml.document" // .docx
				-> "com.google.android.apps.docs.editors.docs"

			"application/vnd.ms-excel", // .xls
			"application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
			"application/vnd.oasis.opendocument.spreadsheet" // .ods
				-> "com.google.android.apps.docs.editors.sheets"

			"application/vnd.ms-powerpoint", // .ppt
			"application/vnd.openxmlformats-officedocument.presentationml.presentation", // .pptx
			"application/vnd.oasis.opendocument.presentation" // .opt
				-> "com.google.android.apps.docs.editors.slides"

			else -> return
		}

		context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId)))
	}

	/**
	 * Opens a file descriptor from the given path, or opens the app store to a google app to handle the intent
	 */
	fun open(file: FileDescriptor, context: Context)
	{
		// open file intent, check without extension for backwards compatibility with older versions of the app
		val path = if (File(MainApplication.BASE_PATH, file.title).exists()) File(MainApplication.BASE_PATH, file.title) else File(MainApplication.BASE_PATH, file.titleWithExtension)
		val contentUri = FileProvider.getUriForFile(context, context.packageName + ".provider", path)

		val shareIntent = Intent()
		shareIntent.action = Intent.ACTION_VIEW
		shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
		shareIntent.setDataAndType(contentUri, file.mime)
		shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)

		val infos = context.packageManager.queryIntentActivities(shareIntent, 0)
		if (infos.size > 0)
		{
			context.startActivity(Intent.createChooser(shareIntent, "Open with"))
		}
		else
		{
			launchStoreForMime(file.mime, context)
		}
	}

	/**
	 * Exports the user's content and directory data into a CSV
	 */
	fun generateUserContent(context: Context, onlyCritical: Boolean = false): String
	{
		val columns = context.resources.getStringArray(R.array.csv_columns)

		var finalCsv = ""
		val criticalPrefs = context.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)
		val notesPrefs = context.getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
		val checkPrefs = context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		DirectoryManager.directories.forEach { directory ->
			val data = arrayListOf<LinkedHashMap<String, String>>()

			directory.directories.forEach { step ->
				step.directories.forEach { substep ->
					val rows = ArrayList<LinkedHashMap<String, String>>()
					rows.add(linkedMapOf<String, String>())
					rows[0].putAll(columns.associate { it to "" })

					rows[0][columns[0]] = ((step.metadata?.get("hierarchy") as String? ?: "${step.order}") + " " + step.title).escapeCsv()
					rows[0][columns[1]] = ((substep.metadata?.get("hierarchy") as String? ?: "${substep.order}") + " " + substep.title).escapeCsv()
					rows[0][columns[2]] = if (checkPrefs.contains(substep.id.toString())) "yes" else "no"
					rows[0][columns[3]] = notesPrefs.getString(substep.id.toString(), "").escapeCsv()

					// tools
					val toolIterator = ({ index: Int, tool: Directory ->
						// add new row
						if (rows.size - 1 < index)
						{
							rows.add(rows[0].clone() as LinkedHashMap<String, String>)
						}

						val capIndex = Math.min(index, rows.size - 1)

						rows[capIndex][columns[4]] = tool.title.escapeCsv()
						rows[capIndex][columns[5]] = if (checkPrefs.contains(tool.id.toString())) "yes" else "no"
						rows[capIndex][columns[6]] = notesPrefs.getString(tool.id.toString(), "").escapeCsv()
					})

					if (onlyCritical)
					{
						val tools = substep.directories
							.filter { it.metadata?.get("critical_path") as Boolean? ?: false || criticalPrefs.contains(it.id.toString()) }

						if (tools.isNotEmpty())
						{
							tools.forEachIndexed(toolIterator)
							data.addAll(rows)
						}
					}
					else
					{
						substep.directories.forEachIndexed(toolIterator)
						data.addAll(rows)
					}
				}
			}

			var directoryCsv = ""
			directoryCsv += directory.title + ("," * (columns.size - 1)) + "\n"
			directoryCsv += columns.joinToString(",") + "\n"

			data.forEach { row ->
				directoryCsv += row.values.joinToString(",") + "\n"
			}

			finalCsv += directoryCsv + "\n"
		}

		finalCsv += "\r\n"
		return finalCsv
	}

	/**
	 * Downloads a given file to a standard export path defined in [MainApplication.BASE_PATH]
	 *
	 * @param file The file to download
	 * @param progress Progress callback for updating the UI
	 * @param callback Finish callback called when operation is completed
	 *
	 * @return The task created for the download operation
	 */
	@SuppressLint("StaticFieldLeak")
	fun download(file: FileDescriptor, path: File, progress: (percent: Int) -> Unit, callback: (success: Boolean, file: File) -> Unit): AsyncTask<Void, Int, Boolean>
	{
		return object : AsyncTask<Void, Int, Boolean>()
		{
			override fun onProgressUpdate(vararg values: Int?)
			{
				progress.invoke(values[0] ?: 0)
			}

			override fun doInBackground(vararg params: Void?): Boolean
			{
				val client = OkHttpClient()

				val rfc1123 = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.US)
				rfc1123.timeZone = TimeZone.getTimeZone("GMT")
				val lastModified = rfc1123.format(Date())//file.timestamp));

				val request = Request.Builder()
					.addHeader("User-Agent", "Android/ARC-" + BuildConfig.APPLICATION_ID + "-" + BuildConfig.VERSION_NAME)
//					.addHeader("Last-Modified", lastModified)
					.addHeader("Cache-Control", "max-age=0")
					.url(file.url)
					.build()

				try
				{
					val response = client.newCall(request).execute()
					val inputStream =  response.body()?.byteStream()

					inputStream?.use { inStream ->
						FileOutputStream(path).use { outStream ->
							var bytesCopied: Long = 0
							val totalBytes: Long = response.body()?.contentLength() ?: 0
							var totalPercent = 0
							val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
							var bytes = inStream.read(buffer)

							while (bytes >= 0 && !isCancelled)
							{
								outStream.write(buffer, 0, bytes)
								bytesCopied += bytes

								try
								{
									bytes = inStream.read(buffer)
								}
								catch (e: Exception)
								{
									return false
								}

								val newPercent = ((bytesCopied.toDouble() / totalBytes.toDouble()) * 100.0).toInt()
								if (newPercent > totalPercent)
								{
									totalPercent = newPercent
									publishProgress(totalPercent)
								}
							}

							return path.length() == totalBytes && response.isSuccessful
						}
					}
				}
				catch (e: Exception)
				{
					e.printStackTrace()
				}

				return false
			}

			override fun onPostExecute(result: Boolean)
			{
				if (!isCancelled)
				{
					callback.invoke(result, path)
				}
				else
				{
					File(MainApplication.BASE_PATH, file.title).delete()
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
	}
}
