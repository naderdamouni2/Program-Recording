package com.example.programrecording.download

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.app.AlarmManagerCompat
import androidx.core.content.getSystemService
import java.util.*


class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.run {
            with((getSystemService(Context.POWER_SERVICE) as PowerManager)) {
                with(newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "AlarmReceiver::WakeLock")) {
                    acquire(10 * 60 * 1000L /*10 minutes*/)

                    intent?.getParcelableExtra<DownloadRequest>(
                        ProgramDownloadService.EXTRA_DOWNLOAD_REQUEST
                    )?.let {
                        val serviceIntent = Intent(
                            context, ProgramDownloadService::class.java
                        ).apply {
                            putExtra(ProgramDownloadService.EXTRA_DOWNLOAD_ALARM, true)
                            putExtra(ProgramDownloadService.EXTRA_DOWNLOAD_REQUEST, it)
                        }
                        return@run startService(serviceIntent)
                    }

                    release()
                }
            }
        }
    }

    companion object {

        fun Context.setupProgramDownloadAlarm(
            downloadRequest: DownloadRequest = DownloadRequest(1231234324, "Title"),
            timeInMs: Long = (1 * 60 * 1000).toLong()
        ) {
            val alarmManager = getSystemService<AlarmManager>()
            // current time in ms + alarm time from now
            val timeFromNow = System.currentTimeMillis() + timeInMs
            // Make sure to put a class which extends BroadcastReceiver as second parameter for Intent
            val intent = Intent(this, AlarmReceiver::class.java)
            intent.putExtra(
                ProgramDownloadService.EXTRA_DOWNLOAD_REQUEST, downloadRequest
            )

            val pIntent = PendingIntent.getBroadcast(
                this,
                downloadRequest.id,
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT // setting the mutability flag
            )
            alarmManager?.let {
                AlarmManagerCompat.setExactAndAllowWhileIdle(
                    it, AlarmManager.RTC_WAKEUP, timeFromNow, pIntent
                )
            }

        }

        fun Context.cancelAlarm(downloadRequest: DownloadRequest) {
            val intent = Intent(this, AlarmReceiver::class.java)
            val sender = PendingIntent.getBroadcast(this, downloadRequest.id, intent, 0)
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.cancel(sender)
        }
    }
}