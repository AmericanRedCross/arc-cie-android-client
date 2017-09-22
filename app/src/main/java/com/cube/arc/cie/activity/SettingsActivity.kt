package com.cube.arc.cie.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.cie.fragment.DownloadHelper
import com.cube.arc.onboarding.activity.VideoPlayerActivity
import com.cube.arc.workflow.model.FileDescriptor
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.util.bind
import com.cube.lib.util.extractTo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader

/**
 * Settings activity to allow users to download content updates or reset app state
 */
class SettingsActivity : AppCompatActivity()
{
	private val contentUpdate by bind<View>(R.id.update_container)
	private val updateButton by bind<Button>(R.id.update_download)
	private val video by bind<View>(R.id.video_container)
	private val reset by bind<View>(R.id.reset_container)
	private val locale by bind<View>(R.id.locale_container)

	private lateinit var downloadTask: DownloadHelper
	private val downloadProgress: ProgressDialog by lazy {
		ProgressDialog(this).also { progress ->
			progress.setMessage("Downloading content update")
		}
	}

	override fun onCreate(savedInstanceState: Bundle?)
	{
		super.onCreate(savedInstanceState)

		AnalyticsHelper.userViewSettings()

		setContentView(R.layout.settings_activity_view)
		setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

		video.setOnClickListener {
			AnalyticsHelper.userTapsTutorialVideo()

			startActivity(Intent(this, VideoPlayerActivity::class.java))
		}

		reset.setOnClickListener {
			AnalyticsHelper.userTapsResetData()

			AlertDialog.Builder(this)
				.setTitle(R.string.reset_dialog_title)
				.setMessage(R.string.reset_dialog_description)
				.setPositiveButton(R.string.reset_dialog_button_confirm, resetData)
				.setNegativeButton(R.string.reset_dialog_button_cancel, null)
				.show()
		}

		// Check for update
		if (File(filesDir, "content-check.json").exists())
		{
			setDownloadUi()
		}
		else
		{
			setCheckUi()
		}
	}

	/**
	 * Sets the check for updates ui
	 */
	fun setCheckUi()
	{
		(contentUpdate.findViewById(R.id.update_download) as TextView).setText(R.string.setting_update_check_button)
		(contentUpdate.findViewById(R.id.update_title) as TextView).setText(R.string.setting_update_check_title)
		(contentUpdate.findViewById(R.id.update_description) as TextView).setText(R.string.setting_update_check_description)

		downloadTask = DownloadHelper.newInstance(this, "content_check")

		if (downloadTask.isDownloading.get())
		{
			updateButton.isEnabled = false
		}

		downloadTask.progressLambda = { progress ->
			updateButton.isEnabled = false
		}

		downloadTask.callbackLambda = { success, filePath ->
			updateButton.isEnabled = true

			if (success)
			{
				val response = Gson().fromJson<Map<Any?, Any?>>(FileReader(filePath), object : TypeToken<Map<Any?, Any?>>(){}.type)
				response.getOrElse("data", { null })?.let {
					downloadTask.detach()

					Toast.makeText(this@SettingsActivity, "There are content updates available to download", Toast.LENGTH_SHORT).show()
					setDownloadUi()
				}
			}
		}

		updateButton.setOnClickListener {
			downloadTask = downloadTask.attach(this)
			downloadTask.file = FileDescriptor(url = "http://ec2-54-193-52-173.us-west-1.compute.amazonaws.com/api/projects/1/publishes/latest")
			downloadTask.execute(outFile = File(filesDir, "content-check.json"))
		}
	}

	/**
	 * Changes the "check content" ui to "download content"
	 */
	fun setDownloadUi()
	{
		(contentUpdate.findViewById(R.id.update_download) as TextView).setText(R.string.setting_update_download_button)
		(contentUpdate.findViewById(R.id.update_title) as TextView).setText(R.string.setting_update_download_title)
		(contentUpdate.findViewById(R.id.update_description) as TextView).setText(R.string.setting_update_download_description)

		downloadTask = DownloadHelper.newInstance(this, "content_update")

		if (downloadTask.isDownloading.get())
		{
			downloadProgress.show()
			updateButton.isEnabled = false
		}

		downloadTask.progressLambda = { progress ->
			updateButton.isEnabled = false
		}

		downloadTask.callbackLambda = { success, filePath ->
			if (success)
			{
				File(filesDir, "content-check.json").delete()

				// extract tar
				Thread({
					filePath.extractTo(filePath.parentFile)
					filePath.delete()

					runOnUiThread {
						downloadProgress.dismiss()
						updateButton.isEnabled = true

						(application as MainApplication).initManagers()

						Toast.makeText(this, "Content successfully updated", Toast.LENGTH_SHORT).show()

						setCheckUi()
					}
				}).start()
			}
			else
			{
				Toast.makeText(this, "There was a problem downloading the content update", Toast.LENGTH_LONG).show()
			}
		}

		updateButton.setOnClickListener {
			downloadProgress.show()
			updateButton.isEnabled = false

			downloadTask = downloadTask.attach(this)
			downloadTask.file = FileDescriptor(url = "http://ec2-54-193-52-173.us-west-1.compute.amazonaws.com/api/projects/1/publishes/latest?redirect=true&language=en")
			downloadTask.execute(outFile = File(filesDir, "content.tar.gz"))
		}
	}

	/**
	 * Dialog interface callback for reset data confirmation popup
	 */
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
