package com.cube.arc.cie.activity

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.cube.arc.BuildConfig
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.cie.fragment.DownloadHelper
import com.cube.arc.dmsdk.model.FileDescriptor
import com.cube.arc.onboarding.activity.VideoPlayerActivity
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.util.bind
import com.cube.lib.util.extractTo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.text.SimpleDateFormat
import java.util.*

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

	private var updateTask: () -> Unit = {}

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

		updateButton.setOnClickListener {
			AlertDialog.Builder(this@SettingsActivity)
				.setTitle(R.string.setting_update_warning_title)
				.setMessage(R.string.setting_update_warning_message)
				.setPositiveButton(R.string.setting_update_warning_positive, { dialog, which ->
					updateTask.invoke()
				})
				.setNegativeButton(R.string.setting_update_warning_negative, null)
				.show()
		}

		locale.setOnClickListener {
			val availableLocales = PreferenceManager.getDefaultSharedPreferences(it.context).getStringSet("languages", setOf("en"))
			val selected = PreferenceManager.getDefaultSharedPreferences(it.context).getString("content_language", "en")
			val index = availableLocales.indexOf(selected)

			val locales = arrayListOf<String>()
			availableLocales.forEach { locales.add(Locale(it).displayLanguage) }

			AlertDialog.Builder(this@SettingsActivity)
				.setTitle(R.string.setting_locale_dialog_title)
				.setSingleChoiceItems(locales.toTypedArray(), index, {dialog, which ->
					dialog.dismiss()

					if (which != index)
					{
						PreferenceManager.getDefaultSharedPreferences(it.context).edit()
							.putString("content_language", availableLocales.toTypedArray()[which])
							.apply()

						setDownloadUi()
						updateButton.performClick()
					}
				})
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
			downloadProgress.dismiss()

			updateButton.isEnabled = true
			downloadTask.detach()

			val latestVersion = ({
				Toast.makeText(this@SettingsActivity, "You are already on the latest content version", Toast.LENGTH_SHORT).show()
				File(filesDir, "content-check.json").delete()
				setCheckUi()
			})

			if (success)
			{
				val response = Gson().fromJson<Map<Any?, Any?>>(FileReader(filePath), object : TypeToken<Map<Any?, Any?>>(){}.type) ?: mapOf()

				response.getOrElse("data", {
					latestVersion.invoke()
				})?.let {
					val data = it as Map<Any?, Any?>
					val languages = data["languages"] as List<String> ?: listOf()

					PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity).edit()
						.putStringSet("languages", languages.toSet())
						.apply()

					var publishDate = data["publish_date"] as String
					val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
					val date = sdf.parse(publishDate).time
					val contentDate = PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity).getLong("content_date", 0)

					if (date > contentDate)
					{
						Toast.makeText(this@SettingsActivity, "There are content updates available to download", Toast.LENGTH_SHORT).show()
						setDownloadUi()
					}
					else
					{
						latestVersion.invoke()
					}
				}
			}
			else
			{
				latestVersion.invoke()
			}
		}

		updateTask = {
			downloadTask = downloadTask.attach(this)
			downloadTask.file = FileDescriptor(url = "${BuildConfig.API_URL}/api/projects/${BuildConfig.PROJECT_ID}/publishes/latest")
			downloadTask.execute(outFile = File(filesDir, "content-check.json"))

			downloadProgress.show()
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
			downloadProgress.dismiss()
			downloadTask.detach()

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
				setDownloadUi()
			}
		}

		updateTask = {
			downloadProgress.show()
			updateButton.isEnabled = false

			var selectedLocale = PreferenceManager.getDefaultSharedPreferences(this@SettingsActivity).getString("content_language", "en")

			downloadTask = downloadTask.attach(this)
			downloadTask.file = FileDescriptor(url = "${BuildConfig.API_URL}/api/projects/${BuildConfig.PROJECT_ID}/publishes/latest?redirect=true&language=$selectedLocale")
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
