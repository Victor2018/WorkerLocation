package com.victor.worker.location

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent


object AlarmUtil {
    fun setAlarm(context: Context, triggerAtMillis: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE)
        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent)
    }
}