package com.victor.worker.location

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import java.text.SimpleDateFormat

object NotificationUtil {

    const val CHANNEL_ID = "WORKER_LOCATION"
    const val CHANNEL_NAME = "WORKER_LOCATION_NAME"
    const val NOTIFICATION_ID = 423099
    const val NOTIFICATION_TIME_OUT_AFTER = 1 * 60 * 1000L//1分钟后通知自动取消
    const val NOTIFICATION_IMPORTANCE = NotificationManager.IMPORTANCE_HIGH

    private val notificationManager by lazy {
        NotificationManagerCompat.from(App.get())
    }

    fun createNotificationChannel() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NOTIFICATION_IMPORTANCE
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun getNotificationBuilder(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setTicker("位置更新提示")//设置状态栏的标题
            .setContentTitle("Tracking location...")//设置标题
            .setContentText("Location: location...")//消息内容
            .setSmallIcon(R.drawable.ic_launcher_background)//小图标
            .setContentIntent(getContentIntent(context))//点击时意图
            .setAutoCancel(true)//点击自动取消
            .setShowWhen(true)//是否显示通知时间
            .setWhen(System.currentTimeMillis())//通知时间
            .setTimeoutAfter(NOTIFICATION_TIME_OUT_AFTER)//定时取消，8.0及以后
            .setVisibility(NotificationCompat.VISIBILITY_SECRET)//屏幕可见性，适用“锁屏状态”
            .setVibrate(longArrayOf(0,300,500,700))//延迟0ms，然后振动300ms，在延迟500ms，接着在振动700ms
            .setOngoing(true)//notification就能够一直停留在系统的通知栏直到cancel或者应用退出
    }

    fun getContentIntent(context: Context): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        val requestCode = System.currentTimeMillis().hashCode()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_MUTABLE)
        } else {
            PendingIntent.getActivity(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        }
    }

    @SuppressLint("MissingPermission")
    fun sendNotification(notificationBuilder: NotificationCompat.Builder,
                         location: Location, address: String) {
        val latitude = location.latitude.toString().takeLast(3)
        val longitude = location.longitude.toString().takeLast(3)
        notificationBuilder.setContentText(
            "Location: ($latitude, $longitude)\naddress:$address"
        )
        // 获取通知管理器
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun cancelNotifications() {
        notificationManager.cancelAll()
    }
}