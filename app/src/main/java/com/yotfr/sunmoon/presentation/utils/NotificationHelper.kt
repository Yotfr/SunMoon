package com.yotfr.sunmoon.presentation.utils

import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.yotfr.sunmoon.R
import com.yotfr.sunmoon.presentation.MainActivity

class NotificationHelper(val context: Context) {

    fun createNotification(title: String, taskId: Long, destination: Int) {
        val intent = Intent(
            Intent.ACTION_VIEW,
            "https://taskDetails/taskId/$taskId/destination/$destination".toUri(),
            context,
            MainActivity::class.java
        )

        val pendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(1, PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_task)
            .setContentTitle(title)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        NotificationManagerCompat.from(context).notify(taskId.toInt(), notification)
    }
}
