package com.cube.arc.cie.fragment

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import com.cube.arc.cie.MainApplication
import com.cube.arc.cie.fragment.DownloadHelper.Companion.newInstance
import com.cube.arc.dmsdk.model.FileDescriptor
import com.cube.arc.workflow.manager.ExportManager
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Headless fragment used for holding the network request and updating the UI for the attached activity.
 * Usage:
 *
 * Calling [newInstance] will create and return an instance of [DownloadHelper], if the instance has already been
 * added to the activity, it will return that original instance (as per [tagStr])
 */
class DownloadHelper : Fragment()
{
	companion object
	{
		/**
		 * Creates a new instance of the download helper fragment, or returns the existing instance for the given
		 * [tagStr]
		 */
		public fun newInstance(activity: AppCompatActivity, tagStr: String = "", file: FileDescriptor? = null): DownloadHelper
		{
			val helper = DownloadHelper().apply {
				this.tagStr = tagStr

				if (file != null)
				{
					this.file = file
				}
			}

			return helper.attach(activity)
		}
	}

	public lateinit var file: FileDescriptor
	public val isDownloading: AtomicBoolean = AtomicBoolean(false)
	private var tagStr: String = ""
	private var downloadTask: AsyncTask<Void, Int, Boolean>? = null
	public var progressLambda: ((Int) -> Unit)? = null
	public var callbackLambda: ((Boolean, File) -> Unit)? = null

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		retainInstance = true
	}

	/**
	 * Attaches the fragment to the given activity, or returns its attached instance if already attached
	 */
	fun attach(activity:AppCompatActivity): DownloadHelper
	{
		if (activity.supportFragmentManager.findFragmentByTag(tagStr) == null)
		{
			activity.supportFragmentManager.beginTransaction()
				.add(this@DownloadHelper, tagStr)
				.commit()

			return this
		}
		else
		{
			return activity.supportFragmentManager.findFragmentByTag(tagStr) as DownloadHelper
		}
	}

	/**
	 * Detaches the fragment from the current added activity, this will cancel any download task
	 */
	fun detach()
	{
		progressLambda = null
		callbackLambda = null

		downloadTask?.cancel(true)
		isDownloading.set(false)

		if (isAdded && activity != null)
		{
			activity.supportFragmentManager.beginTransaction()
				.remove(this@DownloadHelper)
				.commit()
		}
	}

	/**
	 * Executes the download task and updates the UI within [DocumentViewerActivity], can only be called once
	 * during a download.
	 */
	fun execute(outFile: File = File(MainApplication.BASE_PATH, file.title))
	{
		if (isDownloading.get()) return

		downloadTask = ExportManager.download(
			file = file,
			path = outFile,
			progress = { progress ->
				progressLambda?.also { callback ->
					isDownloading.set(true)

					if (isAdded)
					{
						callback.invoke(progress)
					}
				}
			},
			callback = { success, filePath ->
				callbackLambda?.also { callback ->
					isDownloading.set(false)

					if (isAdded)
					{
						callback.invoke(success, filePath)
					}
				}
			}
		)
	}
}
