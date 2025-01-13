package com.victor.worker.location

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationUtil {

    @SuppressLint("MissingPermission")
    fun sendNotification(context: Context?) {
        // 获取通知管理器
        val notificationManager = context?.let { NotificationManagerCompat.from(it) }

        // 创建通知 channel（如果是Android O或更高版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channel_id",
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager?.createNotificationChannel(channel)
        }

        // 创建通知构建器
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context!!, "channel_id")
                .setSmallIcon(com.google.android.material.R.drawable.ic_clock_black_24dp) // 设置小图标
                .setContentTitle("Notification Title") // 设置通知标题
                .setContentText("This is the notification message") // 设置通知内容
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) // 设置通知优先级
        // 发送通知
        val notificationId = 1 // 通知ID，必须是唯一的
        val notification = builder.build()
        notificationManager?.notify(notificationId, notification)
    }
}