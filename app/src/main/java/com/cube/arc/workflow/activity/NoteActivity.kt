package com.cube.arc.workflow.activity

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.cube.arc.R
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.util.bind

/**
 * Activity host for adding/editing a note
 */
class NoteActivity : AppCompatActivity()
{
	private val id: Int = IntentDataHelper.retrieve<Int>(this::class.java)
	private val prefs: SharedPreferences by lazy { getSharedPreferences("cie.notes", Context.MODE_PRIVATE) }
	private var directory: Directory? = null

	private val editor: EditText by bind<EditText>(R.id.editor)
	private val save: View by bind<View>(R.id.save)
	private val actionTitle: TextView by bind<TextView>(R.id.ab_title)
	private val actionSubTitle: TextView by bind<TextView>(R.id.ab_subtitle)
	private val actionIcon: ImageView by bind<ImageView>(R.id.ab_icon)
	private val actionCancel: View by bind<View>(R.id.cancel)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		AnalyticsHelper.userViewsNoteEditor()

		setContentView(R.layout.note_activity_view)

		directory = DirectoryManager.directory(id)

		if (directory == null)
		{
			finish()
			return
		}

		if (savedInstanceState == null)
		{
			editor.setText(prefs.getString(id.toString(), ""))
			editor.setSelection(editor.text.length)
		}

		when
		{
			editor.text.isEmpty() -> {
				actionTitle.text = getString(R.string.note_dialog_title_add)
				actionIcon.setImageResource(R.drawable.ab_ic_add_note)
			}
			else -> {
				actionTitle.text = getString(R.string.note_dialog_title_edit)
				actionIcon.setImageResource(R.drawable.ab_ic_edit_note)
			}
		}

		actionSubTitle.text = directory?.metadata?.get("hierarchy") as String? ?: ""

		actionCancel.setOnClickListener { view ->
			AnalyticsHelper.userCancelsNoteEditor()

			AlertDialog.Builder(view.context)
				.setTitle(R.string.note_dialog_cancel_confirm_title)
				.setMessage(R.string.note_dialog_cancel_confirm_message)
				.setPositiveButton(R.string.note_dialog_cancel_confirm_positive, { dialog, which ->
					finish()
				})
				.setNegativeButton(R.string.note_dialog_cancel_confirm_negative, { dialog, which ->
					dialog.dismiss()
				})
				.show();
		}

		save.setOnClickListener {
			AnalyticsHelper.userCompletesNoteEditor()

			prefs.edit().let {
				when (editor.text.isEmpty())
				{
					true -> it.remove(id.toString())
					else -> it.putString(id.toString(), editor.text.toString())
				}
			}.apply()

			finish()
		}
	}

	override fun onSaveInstanceState(outState: Bundle?)
	{
		IntentDataHelper.store(this::class.java, id)
		super.onSaveInstanceState(outState)
	}

	override fun onBackPressed()
	{
		actionCancel.performClick()
	}
}
