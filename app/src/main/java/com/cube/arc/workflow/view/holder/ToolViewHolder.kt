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
import com.cube.arc.workflow.activity.NoteActivity
import com.cube.arc.workflow.manager.ExportManager
import com.cube.arc.workflow.manager.ModulesManager
import com.cube.arc.workflow.model.Module
import com.cube.lib.helper.IntentDataHelper
import com.cube.lib.util.tint
import java.io.File

/**
 * View holder for module in WorkFlowFragment recycler view
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

	fun populate(module: Module?, tool: Module)
	{
		var notePrefs = itemView.context.getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
		var checkPrefs = itemView.context.getSharedPreferences("cie.checked", Context.MODE_PRIVATE)
		var criticalPrefs = itemView.context.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)

		critical.apply {
			visibility = if (tool.critical || criticalPrefs.contains(tool.id)) View.VISIBLE else View.GONE
			text = resources.getString(if (criticalPrefs.contains(tool.id)) R.string.module_tool_user_critical else R.string.module_tool_critical)
		}

		note.visibility = if (notePrefs.contains(tool.id)) View.VISIBLE else View.GONE

		if (tool.attachments?.isNotEmpty() ?: false)
		{
			exported.visibility = if (ExportManager.isFileDownloaded(tool.attachments!![0])) View.VISIBLE else View.GONE
		}

		toolTitle.text = tool.title
		toolDescription.text = tool.content

//		toolIcon.tint(ModulesManager.moduleColours[module?.hierarchy ?: 1] ?: R.color.module_1)
		toolCheck.tint(ModulesManager.moduleColours[module?.hierarchy ?: 1] ?: R.color.module_1)
		toolCheck.isChecked = checkPrefs.contains(tool.id)
		toolCheck.setOnCheckedChangeListener { buttonView, isChecked ->
			checkPrefs.edit().apply {
				when
				{
					isChecked -> putBoolean(tool.id, true)
					else -> remove(tool.id)
				}
			}.apply()
		}

		options.setOnClickListener { view ->
			val criticalPrefs = view.context.getSharedPreferences("cie.critical", Context.MODE_PRIVATE)
			val notePrefs = view.context.getSharedPreferences("cie.notes", Context.MODE_PRIVATE)
			val popup = PopupMenu(view.context, view)

			popup.menuInflater.inflate(R.menu.menu_tool, popup.menu)

			if (tool.critical)
			{
				popup.menu.findItem(R.id.action_mark).isVisible = false
			}

			popup.menu.findItem(R.id.action_mark).title = when
			{
				criticalPrefs.contains(tool.id) -> view.resources.getString(R.string.tool_menu_unmark)
				else -> view.resources.getString(R.string.tool_menu_mark)
			}

			popup.menu.findItem(R.id.action_note).title = when
			{
				notePrefs.contains(tool.id) -> view.resources.getString(R.string.tool_menu_edit_note)
				else -> view.resources.getString(R.string.tool_menu_add_note)
			}

			popup.setOnMenuItemClickListener { item ->
				when (item.itemId)
				{
					R.id.action_mark -> criticalPrefs.edit().apply {
						when (criticalPrefs.contains(tool.id))
						{
							false -> {
								putBoolean(tool.id, true)
								critical.visibility = View.VISIBLE
							}
							else -> {
								remove(tool.id)
								critical.visibility = View.GONE
							}
						}
					}.apply()

					R.id.action_note -> {
						IntentDataHelper.store(NoteActivity::class.java, tool.id)
						view.context.startActivity(Intent(view.context, NoteActivity::class.java))
					}
				}

				true
			}

			popup.show()
		}

		itemView.setOnClickListener { view ->
			tool.attachments?.get(0)?.let { file ->
				if (ExportManager.isFileDownloaded(file))
				{
					ExportManager.open(file, view.context)
				}
				else
				{
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
}
