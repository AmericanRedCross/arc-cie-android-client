package com.cube.arc.workflow.view.holder

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.*
import com.cube.arc.R
import com.cube.arc.cie.MainApplication
import com.cube.arc.dmsdk.manager.DirectoryManager
import com.cube.arc.dmsdk.model.Directory
import com.cube.arc.workflow.activity.NoteActivity
import com.cube.arc.workflow.manager.ExportManager
import com.cube.lib.helper.AnalyticsHelper
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.util.directoryColours
import com.cube.lib.util.mimeIcon
import com.cube.lib.util.tint
import java.io.File

/**
 * View holder for directory in WorkFlowFragment recycler view
 */
class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
{
	private val toolTitle = itemView.findViewById(R.id.tool_title) as TextView
	private val toolIcon = itemView.findViewById(R.id.tool_icon) as ImageView
	private val toolDescription = itemView.findViewById(R.id.tool_description) as TextView
	private val toolCheck = itemView.findViewById(R.id.tool_check) as CheckBox
	private val critical = itemView.findViewById(R.id.critical_tool) as TextView
	private val note = itemView.findViewById(R.id.note_added) as TextView
	private val exported = itemView.findViewById(R.id.exported) as TextView
	private val options = itemView.findViewById(R.id.options_menu) as ImageButton

	fun populate(directory: Directory?, tool: Directory)
	{
		val notePrefs = itemView.context.getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
		val checkPrefs = itemView.context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)
		val criticalPrefs = itemView.context.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)

		critical.apply {
			visibility = if ((tool.metadata?.getOrElse("critical_path", { false }) as Boolean ?: false) || criticalPrefs.contains(tool.id.toString())) View.VISIBLE else View.GONE
			text = resources.getString(if (criticalPrefs.contains(tool.id.toString())) R.string.directory_tool_user_critical else R.string.directory_tool_critical)
		}

		note.visibility = if (notePrefs.contains(tool.id.toString())) View.VISIBLE else View.GONE

		if (tool.attachments.isNotEmpty())
		{
			exported.visibility = if (ExportManager.isFileDownloaded(tool.attachments[0])) View.VISIBLE else View.GONE
		}

		toolTitle.text = tool.title

		toolDescription.visibility = View.GONE
		tool.attachments.getOrNull(0)?.let {
			toolDescription.text = it.description
			toolDescription.visibility = if (it.description?.isEmpty() ?: true) View.GONE else View.VISIBLE
		}

		toolIcon.setImageResource(tool.attachments.getOrNull(0)?.mimeIcon() ?: R.drawable.ic_mime_misc)
		toolIcon.tint(DirectoryManager.directoryColours[directory?.order ?: 1] ?: R.color.directory_1)
		toolCheck.tint(DirectoryManager.directoryColours[directory?.order ?: 1] ?: R.color.directory_1)
		toolCheck.isChecked = checkPrefs.contains(tool.id.toString())
		toolCheck.setOnCheckedChangeListener { buttonView, isChecked ->
			checkPrefs.edit().apply {
				when
				{
					isChecked -> {
						AnalyticsHelper.userChecksDirectoryCheckbox(tool)
						putBoolean(tool.id.toString(), true)
					}
					else -> {
						AnalyticsHelper.userUnchecksDirectoryCheckbox(tool)
						remove(tool.id.toString())
					}
				}
			}.apply()
		}

		options.setOnClickListener { view ->
			val popup = PopupMenu(view.context, view)

			popup.menuInflater.inflate(R.menu.menu_tool, popup.menu)

			if (tool.metadata?.getOrElse("critical_path", { false }) as Boolean ?: false)
			{
				popup.menu.findItem(R.id.action_mark).isVisible = false
			}

			popup.menu.findItem(R.id.action_mark).title = when
			{
				criticalPrefs.contains(tool.id.toString()) -> view.resources.getString(R.string.tool_menu_unmark)
				else -> view.resources.getString(R.string.tool_menu_mark)
			}

			popup.menu.findItem(R.id.action_note).title = when
			{
				notePrefs.contains(tool.id.toString()) -> view.resources.getString(R.string.tool_menu_edit_note)
				else -> view.resources.getString(R.string.tool_menu_add_note)
			}

			tool.attachments.getOrNull(0)?.let {
				popup.menu.findItem(R.id.action_download).isVisible = true
				popup.menu.findItem(R.id.action_share).isVisible = true

				popup.menu.findItem(R.id.action_download).title = when
				{
					ExportManager.isFileDownloaded(it) -> view.resources.getString(R.string.tool_menu_open)
					else -> view.resources.getString(R.string.tool_menu_download)
				}
			}

			popup.setOnMenuItemClickListener { item ->
				when (item.itemId)
				{
					R.id.action_mark -> criticalPrefs.edit().apply {
						when (criticalPrefs.contains(tool.id.toString()))
						{
							false -> {
								AnalyticsHelper.userMarksToolCritical(tool)
								putBoolean(tool.id.toString(), true)
								critical.visibility = View.VISIBLE
							}
							else -> {
								AnalyticsHelper.userUnmarksToolCritical(tool)
								remove(tool.id.toString())
								critical.visibility = View.GONE
							}
						}
					}.apply()

					R.id.action_share -> {
						tool.attachments.getOrNull(0)?.let { file ->
							AnalyticsHelper.userTapsToolShare(tool)

							val shareUrl = "${tool.title} - ${file.url}"
							view.context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).also { intent ->
								intent.type = "text/plain"
								intent.putExtra(Intent.EXTRA_TEXT, shareUrl)
							}, "Share to"))
						}
					}

					R.id.action_note -> {
						when
						{
							notePrefs.contains(tool.id.toString()) -> AnalyticsHelper.userTapsEditNote(tool)
							else -> AnalyticsHelper.userTapsAddNote(tool)
						}

						IntentDataHelper.store(NoteActivity::class.java, tool.id)
						view.context.startActivity(Intent(view.context, NoteActivity::class.java))
					}

					R.id.action_download -> {
						tool.attachments.getOrNull(0)?.let { file ->
							if (ExportManager.isFileDownloaded(file))
							{
								ExportManager.open(file, view.context)
							}
							else
							{
								AnalyticsHelper.userTapsToolDownload(tool)

								val appContext = view.context.applicationContext
								val exportNotification: NotificationCompat.Builder
								val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

								exportNotification = NotificationCompat.Builder(appContext)
									.setContentText("Downloading file " + file.title)
									.setContentTitle("Downloading")
									.setContentIntent(PendingIntent.getActivity(appContext, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
									.setTicker("Downloading file " + file.title)
									.setPriority(NotificationCompat.PRIORITY_HIGH)
									.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
									.setSmallIcon(android.R.drawable.stat_sys_download)
									.setVibrate(LongArray(0))

								notificationManager.notify(file.url.hashCode(), exportNotification.build())

								ExportManager.download(
									file = file,
									path = File(MainApplication.BASE_PATH, file.title),
									progress = { progress ->
										exportNotification.setProgress(100, progress, false);
										notificationManager.notify(file.url.hashCode(), exportNotification.build());
									},
									callback = { success, outFile ->
										if (success)
										{
											val finishNotification = NotificationCompat.Builder(appContext)
												.setContentText("File " + file.title + " downloaded")
												.setTicker("Download of " + file.title  + " complete")
												.setContentTitle("Download Complete")
												.setContentIntent(PendingIntent.getActivity(appContext, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
												.setSmallIcon(android.R.drawable.stat_sys_download_done)
												.setPriority(NotificationCompat.PRIORITY_HIGH)
												.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
												.setAutoCancel(true)
												.setVibrate(LongArray(0))
												.build()
											notificationManager.notify(file.url.hashCode(), finishNotification)

											ExportManager.registerFileManifest(file)
											ExportManager.open(file, exported.context)

											exported.visibility = View.VISIBLE
										}
										else
										{
											val finishNotification = NotificationCompat.Builder(appContext)
												.setContentText("Failed to download " + file.title)
												.setContentTitle("Download Failed")
												.setContentIntent(PendingIntent.getActivity(appContext, 0, Intent(), PendingIntent.FLAG_UPDATE_CURRENT))
												.setSmallIcon(android.R.drawable.stat_sys_warning)
												.setPriority(NotificationCompat.PRIORITY_HIGH)
												.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
												.setAutoCancel(true)
												.setVibrate(LongArray(0))
												.build()
											notificationManager.notify(file.url.hashCode(), finishNotification)
										}
									}
								)
							}
						}
					}
				}

				true
			}

			popup.show()
		}

		itemView.setOnClickListener { view ->
			//
		}
	}
}
