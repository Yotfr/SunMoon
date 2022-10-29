package com.yotfr.sunmoon
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yotfr.sunmoon.presentation.utils.NotificationHelper

class AlarmReceiver:BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED) == intent.action){

        }else {
            intent.getStringExtra("taskTitle")?.let {
                NotificationHelper(context).createNotification(
                    title = it,
                    message = "me"
                )
            }
        }
    }
}