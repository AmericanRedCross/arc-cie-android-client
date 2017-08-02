package com.cube.lib.parser

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.Html
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.MalformedURLException

class URLImageParser (internal var container: TextView) : Html.ImageGetter
{
	override fun getDrawable(source: String): Drawable
	{
		val urlDrawable = URLDrawable()

		// get the actual source
		val asyncTask = ImageGetterAsyncTask(container, urlDrawable)
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, source)

		return urlDrawable
	}

	open class ImageGetterAsyncTask(view: TextView, urlDrawable: URLDrawable) : AsyncTask<String, Void, Drawable>()
	{
		var sourceView: WeakReference<TextView> = WeakReference(view)
		var destDrawable: WeakReference<URLDrawable> = WeakReference(urlDrawable)

		protected override fun doInBackground(vararg params: String): Drawable?
		{
			val source = params[0]
			return fetchDrawable(source)
		}

		protected override fun onPostExecute(result: Drawable)
		{
			destDrawable.get()?.apply {
				setBounds(0, 0, result.intrinsicWidth, result.intrinsicHeight)
				drawable = result
			}

			sourceView.get()?.apply {
				invalidate()
				setText(getText())
			}
		}

		/***
		 * Get the Drawable from URL
		 * @param urlString
		 * @return
		 */
		fun fetchDrawable(urlString: String): Drawable?
		{
			val inputStream = fetch(urlString)

			return inputStream?.let { it ->
				val drawable = BitmapDrawable(sourceView.get()?.resources, it)
				drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

				return drawable
			}
		}

		@Throws(MalformedURLException::class, IOException::class)
		private fun fetch(urlString: String): InputStream?
		{
			val client = OkHttpClient()

			val request = Request.Builder()
				.url(urlString)
				.build()

			val response = client.newCall(request).execute()
			return response.body()?.byteStream()
		}
	}
}
