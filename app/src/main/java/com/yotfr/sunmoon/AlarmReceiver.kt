package com.yotfr.sunmoon
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.yotfr.sunmoon.domain.usecase.task.TaskUseCase
import com.yotfr.sunmoon.presentation.utils.NotificationHelper
import com.yotfr.sunmoon.presentation.utils.goAsync
import javax.inject.Inject

class AlarmReceiver : BroadcastReceiver() {

    @Inject lateinit var taskUseCase: TaskUseCase

    // receive tasks notifications
    override fun onReceive(context: Context, intent: Intent) {
        if ((Intent.ACTION_BOOT_COMPLETED) == intent.action) {
            // recreate all notification when device rebooted
            goAsync {
                val tasks = taskUseCase.getAllRemindedTasks()
                tasks.forEach {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val newIntent = Intent(context, AlarmReceiver::class.java)
                    newIntent.putExtra("taskTitle", it.taskDescription)
                    newIntent.putExtra("taskId", it.taskId)
                    // destination 0 means scheduled task list and used to navigate correctly with deeplink
                    newIntent.putExtra("destination", 0)
                    val pendingIntent = it.taskId?.let { it1 ->
                        PendingIntent.getBroadcast(
                            context,
                            it1.toInt(),
                            newIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                        )
                    }
                    it.remindDelayTime?.let { it1 ->
                        alarmManager.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            it1,
                            pendingIntent
                        )
                    }
                }
            }
        } else {
            val taskTitle = intent.getStringExtra("taskTitle")
            val taskId = intent.getLongExtra("taskId", 0)
            // destination 0 means scheduled task list and used to navigate correctly with deeplink
            val destination = intent.getIntExtra("destination", 0)
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
