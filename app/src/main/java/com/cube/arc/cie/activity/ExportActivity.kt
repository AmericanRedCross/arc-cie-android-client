package com.cube.arc.cie.activity;

import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import com.cube.arc.R
import com.cube.lib.util.bind
import com.cube.lib.util.inflate
import kotlinx.android.synthetic.main.document_viewer_activity_view.*

/**
 * Export activity used for exporting content from within the app
 */
class ExportActivity : AppCompatActivity()
{
	private val exportablesContainer by bind<LinearLayout>(R.id.exportables_container)
//	private var downloadTask: DownloadHelper = DownloadHelper()

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.export_activity_view)

//		downloadTask = downloadTask.attach(this)

		setUi()
	}

	fun setUi()
	{
		// critical path tools
		inflateExportable(
			exportTitle = R.string.export_critical_title,
			exportClick = { export, progress ->

			}
		)

		// entire toolkit
		inflateExportable(
			exportTitle = R.string.export_toolkit_title,
			exportClick = { export, progress ->

			}
		)

		// your progress
		inflateExportable(
			exportTitle = R.string.export_progress_title,
			exportClick = { export, progress ->

			}
		)

		// your notes
		inflateExportable(
			exportTitle = R.string.export_notes_title,
			exportClick = { export, progress ->

			}
		)

		close.setOnClickListener {
			finish()
		}
	}

	private fun inflateExportable(@StringRes exportTitle: Int, exportClick: (View, ProgressBar) -> Unit): View
	{
		val view = exportablesContainer.inflate<View>(R.layout.exportable_view_stub).apply {
			val title = findViewById(R.id.document_title) as TextView
			val size = findViewById(R.id.document_size) as TextView
			val progress = findViewById(R.id.download_progress) as ProgressBar
			val export = findViewById(R.id.export) as Button

			title.setText(exportTitle)
			export.setOnClickListener { v ->
				exportClick.invoke(v, progress)
			}
		}

		exportablesContainer.addView(view)
		return view
	}

	override fun finish()
	{
		super.finish()
//		downloadTask.detach()
	}
}
