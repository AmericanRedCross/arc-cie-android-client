package com.cube.lib.parser

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.text.Html
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.ref.WeakReference

/**
 * Parser class for loading <img> tags in html spannables
 */
class URLImageParser(internal var container: TextView) : Html.ImageGetter
{
	override fun getDrawable(source: String): Drawable
	{
		val urlDrawable = URLDrawable(container.resources)

		// get the actual source
		val asyncTask = ImageGetterAsyncTask(container, urlDrawable)
		asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, source)

		return urlDrawable
	}

	/**
	 * Drawable holder class
	 */
	class URLDrawable(res: Resources) : BitmapDrawable(res, null as Bitmap?)
	{
		// the drawable that you need to set, you could set the initial drawing
		// with the loading image if you need to
		public var drawable: Drawable? = null

		override fun draw(canvas: Canvas)
		{
			drawable?.draw(canvas)
		}
	}

	/**
	 * Async class for loading images via http
	 */
	class ImageGetterAsyncTask(view: TextView, urlDrawable: URLDrawable) : AsyncTask<String, Void, Drawable>()
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
		 * @param urlString The Url to load
		 * @return The loaded drawable, or null if the request failed
		 */
		fun fetchDrawable(urlString: String): Drawable?
		{
			val client = OkHttpClient()

			val request = Request.Builder()
				.url(urlString)
				.build()

			val response = client.newCall(request).execute()
			val inputStream =  response.body()?.byteStream()

			return inputStream?.let { it ->
				val drawable = BitmapDrawable(sourceView.get()?.resources, it)
				drawable.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)

				return drawable
			}
		}
	}
}
