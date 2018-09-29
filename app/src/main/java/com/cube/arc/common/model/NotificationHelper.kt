package com.cube.arc.common.model

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import android.support.v4.app.NotificationCompat

class NotificationHelper(base: Context?) : ContextWrapper(base) {
    companion object {
        val CHANNEL_ID = "com.cube.arc"
    }

    val CHANNEL_NAME = "Primary channel"

    init {
        createChannels()
    }

    fun createChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNotificationBuilder(channelId: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(applicationContext, channelId)
    }
}