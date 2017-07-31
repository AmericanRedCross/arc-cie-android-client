package com.cube.arc.cie.activity;

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.cube.arc.R
import com.cube.lib.util.bind

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
	private val export by bind<Button>(R.id.export)
	private val downloadProgress by bind<ProgressBar>(R.id.download_progress)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.document_viewer_activity_view)
	}
}
