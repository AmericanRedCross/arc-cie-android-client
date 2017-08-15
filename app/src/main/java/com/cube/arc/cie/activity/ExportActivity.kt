package com.cube.arc.cie.activity;

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.cube.arc.R
import com.cube.arc.cie.fragment.DownloadHelper
import com.cube.arc.workflow.model.FileDescriptor
import com.cube.lib.util.bind
import com.cube.lib.util.inflate
import kotlinx.android.synthetic.main.document_viewer_activity_view.*

/**
 * Export activity used for exporting content from within the app
 */
class ExportActivity : AppCompatActivity()
{
	private val exportablesContainer by bind<LinearLayout>(R.id.exportables_container)
	private var downloadTasks: HashMap<String, DownloadHelper> = hashMapOf()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.export_activity_view)
		setUi()
	}

	fun setUi()
	{
		// critical path tools
		inflateExportable(
			exportTitle = R.string.export_critical_title,
			exportTask = downloadTasks.getOrPut("critical", {
				DownloadHelper.newInstance(this@ExportActivity, "critical")
			}).apply {
				file = FileDescriptor(
					title = "critital-tools",
					url = "https://httpbin.org/bytes/${1024 * 1024 * 4}",
					size = 1024 * 1024 * 4
				)
			}
		)

//		// entire toolkit
//		inflateExportable(
//			exportTitle = R.string.export_toolkit_title,
//			exportClick = { export, progress ->
//
//			}
//		)
//
//		// your progress
//		inflateExportable(
//			exportTitle = R.string.export_progress_title,
//			exportClick = { export, progress ->
//
//			}
//		)
//
//		// your notes
//		inflateExportable(
//			exportTitle = R.string.export_notes_title,
//			exportClick = { export, progress ->
//
//			}
//		)

		close.setOnClickListener {
			finish()
		}
	}

	private fun inflateExportable(@StringRes exportTitle: Int, exportTask: DownloadHelper)
	{
		val view = exportablesContainer.inflate<View>(R.layout.exportable_view_stub).apply {
			val title = findViewById(R.id.document_title) as TextView
			val size = findViewById(R.id.document_size) as TextView
			val progressBar = findViewById(R.id.download_progress) as ProgressBar
			val export = findViewById(R.id.export) as Button

			if (exportTask.isDownloading.get())
			{
				progressBar.visibility = View.VISIBLE
				export.isEnabled = false
			}

			exportTask.progressLambda = { progress ->
				progressBar.visibility = View.VISIBLE
				progressBar.progress = progress
			}

			exportTask.callbackLambda = { success, file ->
				Toast.makeText(this@ExportActivity, "File exported to ${file.absolutePath}", Toast.LENGTH_SHORT).show()

				progressBar.visibility = View.GONE
				progressBar.progress = 0
				export.isEnabled = true
			}

			title.setText(exportTitle)
			export.setOnClickListener { v ->
				exportTask.execute()

				progressBar.visibility = View.VISIBLE
				export.isEnabled = false
			}
		}

		exportablesContainer.addView(view)
	}

	override fun finish()
	{
		downloadTasks.values.forEach { value ->
			value.detach()
		}

		super.finish()
	}
}
