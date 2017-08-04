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
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream

/**
 * Manager class used for downloading/opening files associated with modules and tools
 */
object ExportManager
{
	init
	{
		MainApplication.BASE_PATH.mkdirs()
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
	fun open(file: FileDescriptor, path: File, context: Context)
	{
		// open file intent
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

				val response = client.newCall(request).execute()
				val inputStream =  response.body()?.byteStream()

				inputStream?.use { inStream ->
					FileOutputStream(path).use { outStream ->
						var bytesCopied: Long = 0
						var totalBytes: Long = response.body()?.contentLength() ?: 0
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

							var newPercent = ((bytesCopied.toDouble() / totalBytes.toDouble()) * 100.0).toInt()
							if (newPercent > totalPercent)
							{
								totalPercent = newPercent
								publishProgress(totalPercent)
							}
						}

						return path.length() == totalBytes && response.isSuccessful
					}
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
