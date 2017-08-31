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
import android.widget.Toast
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.cie.fragment.DownloadHelper
import com.cube.arc.onboarding.activity.VideoPlayerActivity
import com.cube.arc.workflow.model.FileDescriptor
import com.cube.lib.util.bind
import org.kamranzafar.jtar.TarInputStream
import java.io.*
import java.util.zip.GZIPInputStream


/**
 * // TODO: Add class description
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

		setContentView(R.layout.settings_activity_view)
		setSupportActionBar(findViewById(R.id.toolbar) as Toolbar)

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
				// extract tar
				Thread({
					try
					{
						val buffer = 8192
						var totalRead: Long = 0

						val stream = BufferedInputStream(GZIPInputStream(FileInputStream(filePath), buffer), buffer)
						val tis = TarInputStream(stream)

						while (true)
						{
							val file = tis.nextEntry ?: break
							if (file.name == "./") continue

							val extractedFilePath = filePath.parent + "/" + file.name
							val extractFile = File(extractedFilePath)

							if (file.isDirectory)
							{
								extractFile.mkdirs()
								continue
							}

							// create folders if they do not exist for file
							if (!File(extractFile.parent).exists())
							{
								File(extractFile.parent).mkdirs()
							}

							val fos = FileOutputStream(extractedFilePath)
							val dest = BufferedOutputStream(fos, buffer)

							var count = 0
							val data = ByteArray(buffer)

							while (true)
							{
								count = tis.read(data)

								if (count == -1) break

								dest.write(data, 0, count)
								totalRead += count.toLong()
							}

							dest.flush()
							dest.close()
						}

						tis.close()
						filePath.exists()
					}
					catch (e: IOException)
					{
						e.printStackTrace()
					}

					runOnUiThread {
						downloadProgress.dismiss()
						updateButton.isEnabled = true

						Toast.makeText(this, "Content successfully updated", Toast.LENGTH_SHORT).show()
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

			(application as MainApplication).initManagers()
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
