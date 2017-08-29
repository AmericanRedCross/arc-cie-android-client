package com.cube.arc.cie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.cube.arc.R
import com.cube.arc.onboarding.activity.VideoPlayerActivity
import com.cube.lib.util.bind

/**
 * Settings activity to allow users to download content updates or reset app state
 */
class SettingsActivity : AppCompatActivity()
{
	private val contentUpdate by bind<View>(R.id.update_container)
	private val video by bind<View>(R.id.video_container)
	private val reset by bind<View>(R.id.reset_container)
	private val locale by bind<View>(R.id.locale_container)

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)
		setContentView(R.layout.settings_activity_view)

		video.setOnClickListener {
			startActivity(Intent(this, VideoPlayerActivity::class.java))
		}

		reset.setOnClickListener {
			AlertDialog.Builder(this)
				.setTitle(R.string.reset_dialog_title)
				.setMessage(R.string.reset_dialog_description)
				.setPositiveButton(R.string.reset_dialog_button_confirm, resetData)
				.setNegativeButton(R.string.reset_dialog_button_cancel, null)
				.show()
		}
	}

	val resetData = ({ dialog: DialogInterface, index: Int ->
		val criticalPrefs = getSharedPreferences("cie.critical", Context.MODE_PRIVATE)
		val notePrefs = getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
		val checkPrefs = getSharedPreferences("cie.checked", Context.MODE_PRIVATE)

		criticalPrefs.edit().clear().apply()
		notePrefs.edit().clear().apply()
		checkPrefs.edit().clear().apply()

		Toast.makeText(this, R.string.reset_complete_toast, Toast.LENGTH_SHORT).show()
	})
}
