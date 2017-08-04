package com.cube.arc.cie.activity;

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.widget.*
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.workflow.manager.ExportManager
import com.cube.arc.workflow.model.FileDescriptor
import com.cube.arc.workflow.model.Module
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.parser.URLImageParser
import com.cube.lib.util.bind
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Document viewer activity used for viewing content preview of a document
 */
class DocumentViewerActivity : AppCompatActivity()
{
	private val title by bind<TextView>(R.id.title)
	private val preview by bind<EditText>(R.id.preview)
	private val documentIcon by bind<ImageView>(R.id.mime_icon)
	private val documentTitle by bind<TextView>(R.id.document_title)
	private val documentSize by bind<TextView>(R.id.document_size)
	private val close by bind<View>(R.id.close)
	private val export by bind<Button>(R.id.export)
	private val downloadProgress by bind<ProgressBar>(R.id.download_progress)
	private lateinit var module: Module
	private var downloadTask: DownloadHelper = DownloadHelper()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.document_viewer_activity_view)

		module = IntentDataHelper.retrieve(this::class.java)
		downloadTask = downloadTask.attach(this)

		setUi()
	}

	override fun onSaveInstanceState(outState: Bundle?)
	{
		super.onSaveInstanceState(outState)
		IntentDataHelper.store(this::class.java, module)
	}

	fun setUi()
	{
		val files = module.attachments?.filter { file -> file.featured }

		if (files?.isEmpty() ?: true)
		{
			finish()
			return
		}

		val parser = Parser.builder().build()
		val document = parser.parse(module.content)
		val renderer = HtmlRenderer.builder().build()
		val htmlContent = renderer.render(document)

		preview.setText(Html.fromHtml(htmlContent, URLImageParser(preview), null))

		files?.let {
			title.text = files[0].title
			documentTitle.text = files[0].title
			documentSize.text = "%.2fMB".format(files[0].size.toDouble() / 1024.0 / 1024.0)

			downloadProgress.isIndeterminate = true

			if (downloadTask.isDownloading.get())
			{
				export.isEnabled = false
				downloadProgress.visibility = View.VISIBLE
			}

			export.setText(if (ExportManager.isFileDownloaded(files[0])) R.string.document_button_open else R.string.document_button_export)
			export.setOnClickListener {
				if (ExportManager.isFileDownloaded(files[0]))
				{
					ExportManager.open(files[0], File(MainApplication.BASE_PATH, files[0].title), this)
				}
				else
				{
					export.isEnabled = false
					downloadProgress.visibility = View.VISIBLE
					downloadProgress.progress = 0

					downloadTask = downloadTask.attach(this@DocumentViewerActivity)
					downloadTask.file = files[0]
					downloadTask.execute()
				}
			}
		}

		close.setOnClickListener {
			finish()
		}
	}

	override fun finish()
	{
		super.finish()
		downloadTask.detach()
	}

	/**
	 * Headless fragment used for holding the network request and updating the UI for the attached activity
	 */
	class DownloadHelper() : Fragment()
	{
		public lateinit var file: FileDescriptor
		public var isDownloading = AtomicBoolean(false)
		private var downloadTask: AsyncTask<Void, Int, Boolean>? = null

		override fun onCreate(savedInstanceState: Bundle?)
		{
			super.onCreate(savedInstanceState)
			retainInstance = true
		}

		/**
		 * Attaches the fragment to the given activity, or returns its attached instance if already attached
		 */
		fun attach(activity: AppCompatActivity): DownloadHelper
		{
			if (activity.supportFragmentManager.findFragmentByTag("download_task") == null)
			{
				activity.supportFragmentManager.beginTransaction()
					.add(this@DownloadHelper, "download_task")
					.commit()

				return this
			}
			else
			{
				return activity.supportFragmentManager.findFragmentByTag("download_task") as DownloadHelper
			}
		}

		/**
		 * Detaches the fragment from the current added activity, this will cancel any download task
		 */
		fun detach()
		{
			if (isAdded && activity != null)
			{
				downloadTask?.cancel(true)
				isDownloading.set(false)

				activity.supportFragmentManager.beginTransaction()
					.remove(this@DownloadHelper)
					.commit()
			}
		}

		/**
		 * Executes the download task and updates the UI within [DocumentViewerActivity], can only be called once
		 * during a download.
		 */
		fun execute()
		{
			if (isDownloading.get()) return

			downloadTask = ExportManager.download(
				file = file,
				progress = { progress ->
					if (isAdded)
					{
						with (activity as DocumentViewerActivity)
						{
							isDownloading.set(true)

							export.isEnabled = false
							downloadProgress.visibility = View.VISIBLE
							downloadProgress.isIndeterminate = false
							downloadProgress.max = 100
							downloadProgress.progress = progress
							downloadProgress.invalidate()
						}
					}
				},
				callback = { success, filePath ->
					if (isAdded)
					{
						with (activity as DocumentViewerActivity)
						{
							isDownloading.set(false)

							export.isEnabled = true
							downloadProgress.visibility = View.INVISIBLE

							if (success)
							{
								Toast.makeText(activity, "File downloaded successfully", Toast.LENGTH_SHORT).show()

								export.setText(R.string.document_button_open)

								ExportManager.registerFileManifest(file)
								ExportManager.open(file, filePath, activity)
							}
							else
							{
								Toast.makeText(activity, "There was a problem downloading the file", Toast.LENGTH_LONG).show()
							}

							detach()
						}
					}
				}
			)
		}
	}
}
