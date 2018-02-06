package com.cube.arc.onboarding.fragment

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.cube.arc.BuildConfig
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.cie.activity.MainActivity
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

/**
 * Fragment that hosts the UI component of the onboarding feature
 */
class OnboardingFragment : Fragment()
{
	private val videoButton by bind<Button>(R.id.video)
	private val skipButton by bind<Button>(R.id.skip)

	companion object
	{
		public const val REQUEST_WATCH_VIDEO = 1;
	}

	private lateinit var downloadTask: DownloadHelper
	private val downloadProgress: ProgressDialog by lazy {
		ProgressDialog(activity).apply {
			setMessage(activity.getString(R.string.onboarding_setup_progress))
			isIndeterminate = true
		}
	}

	override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? = inflater?.inflate(R.layout.onboarding_fragment_view, container, false)

	override fun onActivityCreated(savedInstanceState: Bundle?)
	{
		super.onActivityCreated(savedInstanceState)

		skipButton.setOnClickListener {
			checkContent()
		}

		videoButton.setOnClickListener {
			AnalyticsHelper.userTapsTutorialVideo()
			startActivityForResult(Intent(activity, VideoPlayerActivity::class.java), REQUEST_WATCH_VIDEO)
		}
	}

	fun checkContent()
	{
		downloadTask = DownloadHelper.newInstance(activity as AppCompatActivity, "initial_content")
		downloadTask.file = FileDescriptor(url = "${BuildConfig.API_URL}/api/projects/${BuildConfig.PROJECT_ID}/publishes/latest")

		if (downloadTask.isDownloading.get())
		{
			downloadProgress.show()
			skipButton.isEnabled = false
		}

		downloadTask.progressLambda = { progress ->
			skipButton.isEnabled = false
		}

		downloadTask.callbackLambda = { success, filePath ->
			downloadTask.detach()

			if (success)
			{
				val response = Gson().fromJson<Map<Any?, Any?>>(FileReader(filePath), object : TypeToken<Map<Any?, Any?>>(){}.type)
				if (response.containsKey("data"))
				{
					val data = response["data"] as Map<Any?, Any?>

					val locale = context.resources.configuration.locale.language.toLowerCase()
					val languages = data["languages"] as List<String> ?: listOf()
					var selectedLocale = languages.find { it.toLowerCase() == locale } ?: "en"

					var publishDate = data["publish_date"] as String
					val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
					val date = sdf.parse(publishDate)

					PreferenceManager.getDefaultSharedPreferences(activity).edit()
						.putLong("content_date", date.time)
						.putString("content_language", selectedLocale)
						.putStringSet("languages", languages.toSet())
						.apply()

					downloadContent((data["download_url"] as String) + "&language=$selectedLocale")
				}
				else
				{
					errorAndReset()
				}
			}
			else
			{
				errorAndReset()
			}
		}

		downloadTask.execute(outFile = File(activity.filesDir, "content-check.json"))
		downloadProgress.show()
	}

	fun downloadContent(url: String)
	{
		downloadTask = DownloadHelper.newInstance(activity as AppCompatActivity, "content_download")
		downloadTask.file = FileDescriptor(url = url)

		if (downloadTask.isDownloading.get())
		{
			downloadProgress.show()
			skipButton.isEnabled = false
		}

		downloadTask.progressLambda = { progress ->
			skipButton.isEnabled = false
		}

		downloadTask.callbackLambda = { success, filePath ->
			if (success)
			{
				File(activity.filesDir, "content-check.json").delete()

				// extract tar
				Thread({
					filePath.extractTo(filePath.parentFile)
					filePath.delete()

					activity.runOnUiThread {
						downloadProgress.dismiss()
						skipButton.isEnabled = true

						(activity.application as MainApplication).initManagers()

						startMain()
					}
				}).start()
			}
			else
			{
				downloadProgress.dismiss()
				Toast.makeText(activity, "There was a problem downloading the content update", Toast.LENGTH_LONG).show()
			}
		}

		downloadTask.execute(outFile = File(activity.filesDir, "content.tar.gz"))
		downloadProgress.show()
	}

	fun errorAndReset()
	{
		skipButton.isEnabled = true
		Toast.makeText(activity, "There was a problem setting up the app, please try again", Toast.LENGTH_LONG).show()
	}

	fun startMain()
	{
		AnalyticsHelper.userTapsOnboardingSkip()
		PreferenceManager.getDefaultSharedPreferences(activity).edit()
			.putBoolean("seen_onboarding", true)
			.apply()

		startActivity(Intent(activity, MainActivity::class.java))
		activity.finish()
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
	{
		if (requestCode == REQUEST_WATCH_VIDEO) skipButton.performClick()
	}
}
