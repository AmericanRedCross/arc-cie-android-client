package com.cube.arc.cie.activity;

import android.content.Intent
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.workflow.manager.ExportManager
import com.cube.lib.util.bind
import com.cube.lib.util.inflate
import kotlinx.android.synthetic.main.document_viewer_activity_view.*
import java.io.File

/**
 * Export activity used for exporting content from within the app
 */
class ExportActivity : AppCompatActivity()
{
	private val exportablesContainer by bind<LinearLayout>(R.id.exportables_container)

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
			exportClick = { view ->
				val shareUrl = "TODO://CHANGE_URL"
				startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).also { intent ->
					intent.type = "text/plain"
					intent.putExtra(Intent.EXTRA_TEXT, shareUrl)
				}, "Share to"))
			}
		)

		// entire toolkit
		inflateExportable(
			exportTitle = R.string.export_toolkit_title,
			exportClick = { view ->
				val shareUrl = "TODO://CHANGE_URL"
				startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).also { intent ->
					intent.type = "text/plain"
					intent.putExtra(Intent.EXTRA_TEXT, shareUrl)
				}, "Share to"))
			}
		)

		// your progress
		inflateExportable(
			exportTitle = R.string.export_progress_title,
			exportClick = { view ->
				val toShare = ExportManager.generateUserContent(view.context)
				val path = File(MainApplication.BASE_PATH, "user_content.csv")
				path.bufferedWriter().use { out ->
					out.write(toShare, 0, toShare.length)
				}

				val contentUri = FileProvider.getUriForFile(view.context, view.context.packageName + ".provider", path)

				startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).also { intent ->
					intent.data = contentUri
					intent.type = "text/csv"
					intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
					intent.putExtra(Intent.EXTRA_STREAM, contentUri)
				}, "Share to"))
			}
		)

		close.setOnClickListener {
			finish()
		}
	}

	private fun inflateExportable(@StringRes exportTitle: Int, exportClick: (View) -> Unit)
	{
		val view = exportablesContainer.inflate<View>(R.layout.exportable_view_stub).apply {
			val title = findViewById(R.id.document_title) as TextView
			val size = findViewById(R.id.document_size) as TextView
			val export = findViewById(R.id.export) as Button

			title.setText(exportTitle)
			export.setOnClickListener(exportClick)
		}

		exportablesContainer.addView(view)
	}
}
