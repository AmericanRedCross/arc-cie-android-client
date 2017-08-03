package com.cube.arc.workflow.manager

import android.annotation.SuppressLint
import android.os.AsyncTask
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

	@SuppressLint("StaticFieldLeak")
	fun download(file: FileDescriptor, progress: (percent: Int) -> Unit, callback: () -> Unit): AsyncTask<Void, Int, Void>
	{
		return object : AsyncTask<Void, Int, Void>()
		{
			override fun onProgressUpdate(vararg values: Int?)
			{
				progress.invoke(values[0] ?: 0)
			}

			override fun doInBackground(vararg params: Void?): Void?
			{
				val client = OkHttpClient()

				val request = Request.Builder()
					.addHeader("User-Agent", "Android/ARC-" + BuildConfig.APPLICATION_ID + "-" + BuildConfig.VERSION_NAME)
					.url(file.url)
					.build()

				val response = client.newCall(request).execute()
				val inputStream =  response.body()?.byteStream()
				val path = File(MainApplication.BASE_PATH, file.title)

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
								break;
							}

							var newPercent = ((bytesCopied.toDouble() / totalBytes.toDouble()) * 100.0).toInt()
							if (newPercent > totalPercent)
							{
								totalPercent = newPercent
								publishProgress(totalPercent)
							}
						}
					}
				}

				return null
			}

			override fun onPostExecute(result: Void?)
			{
				if (!isCancelled)
				{
					callback.invoke()
				}
				else
				{
					File(MainApplication.BASE_PATH, file.title).delete()
				}
			}
		}.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
	}
}
