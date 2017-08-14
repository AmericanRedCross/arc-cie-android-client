package com.cube.arc.workflow.manager

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v4.content.FileProvider
import com.cube.arc.BuildConfig
import com.cube.arc.cie.MainApplication
import com.cube.arc.workflow.model.FileDescriptor
import com.cube.arc.workflow.model.Registry
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader

/**
 * Manager class used for downloading/opening files associated with modules and tools
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
		if (!File(MainApplication.BASE_PATH, REGISTRY).exists()) return false

		val registry = Gson().fromJson<ArrayList<Registry>>(FileReader(File(MainApplication.BASE_PATH, REGISTRY)), object : TypeToken<List<Registry>>(){}.type)
		return registry.filter{ it.fileName == file.title }.filter { it.timestamp >= file.timestamp && File(MainApplication.BASE_PATH, it.fileName).exists() }.isNotEmpty()
	}

	/**
	 * Registers a downloaded file in a manifest used to calculate if the file is the newest version of
	 * a given [FileDescriptor]
	 */
	fun registerFileManifest(file: FileDescriptor)
	{
		var registry = arrayListOf<Registry>()

		if (File(MainApplication.BASE_PATH, REGISTRY).exists())
		{
			registry = Gson().fromJson<ArrayList<Registry>>(FileReader(File(MainApplication.BASE_PATH, REGISTRY)), object : TypeToken<ArrayList<Registry>>(){}.type)
		}

		val fileRegistry = Registry(file.title, file.timestamp)
		registry.add(fileRegistry)

		File(MainApplication.BASE_PATH, REGISTRY).bufferedWriter().use { out -> out.write(Gson().toJson(registry).toString()) }
	}

	/**
	 * Launches google play with specific app query for a given mime type
	 */
	fun launchStoreForMime(mimeType: String, context: Context)
	{
		val appId = when (mimeType.toLowerCase())
		{
			"application/pdf" -> "com.google.android.apps.pdfviewer"
			else -> return
		}

		context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appId)))
	}

	/**
	 * Opens a file descriptor from the given path, or opens the app store to a google app to handle the intent
	 */
	fun open(file: FileDescriptor, context: Context)
	{
		// open file intent
		val path = File(MainApplication.BASE_PATH, file.title)
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
	 * Downloads a given file to a standard export path defined in [MainApplication.BASE_PATH]
	 *
	 * @param file The file to download
	 * @param progress Progress callback for updating the UI
	 * @param callback Finish callback called when operation is completed
	 *
	 * @return The task created for the download operation
	 */
	@SuppressLint("StaticFieldLeak")
	fun download(file: FileDescriptor, progress: (percent: Int) -> Unit, callback: (success: Boolean, file: File) -> Unit): AsyncTask<Void, Int, Boolean>
	{
		return object : AsyncTask<Void, Int, Boolean>()
		{
			val path = File(MainApplication.BASE_PATH, file.title)

			override fun onProgressUpdate(vararg values: Int?)
			{
				progress.invoke(values[0] ?: 0)
			}

			override fun doInBackground(vararg params: Void?): Boolean
			{
				val client = OkHttpClient()

				val request = Request.Builder()
					.addHeader("User-Agent", "Android/ARC-" + BuildConfig.APPLICATION_ID + "-" + BuildConfig.VERSION_NAME)
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
				catch (e: Exception){}

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
