package com.cube.arc.cie.activity;

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Html
import android.view.View
import android.widget.*
import com.cube.arc.R
import com.cube.arc.workflow.model.Module
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.parser.URLImageParser
import com.cube.lib.util.bind
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

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

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		setContentView(R.layout.document_viewer_activity_view)

		module = IntentDataHelper.retrieve(this::class.java)

		setUi()
	}

	fun setUi()
	{
		val files = module.attachments?.filter { file -> file.featured }

		val parser = Parser.builder().build()
		val document = parser.parse(module.content)
		val renderer = HtmlRenderer.builder().build()
		val htmlContent = renderer.render(document)

		preview.setText(Html.fromHtml(htmlContent, URLImageParser(preview), null))

		files?.let {
			title.text = files[0].title
			documentTitle.text = files[0].title
			documentSize.text = "%.2fMB".format(files[0].size.toDouble() / 1024.0 / 1024.0)
		}

		close.setOnClickListener {
			finish()
		}
	}
}
