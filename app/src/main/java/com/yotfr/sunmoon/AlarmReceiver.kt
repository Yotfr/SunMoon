package com.yotfr.sunmoon
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yotfr.sunmoon.presentation.utils.NotificationHelper

class AlarmReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED) == intent.action){

        }else {
            val taskTitle = intent.getStringExtra("taskTitle")
            val taskId = intent.getLongExtra("taskId", 0)
            val destination = intent.getIntExtra("destination",0)
            taskTitle?.let {
                NotificationHelper(context).createNotification(
                    title = it,
                    taskId = taskId,
                    destination = destination
                )
            }
        }
    }
}