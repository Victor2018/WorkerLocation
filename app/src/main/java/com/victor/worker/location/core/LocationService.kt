package com.victor.worker.location.core
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.victor.worker.location.App
import com.victor.worker.location.LocationUtils
import com.victor.worker.location.NotificationUtil
import com.victor.worker.location.NotificationUtil.cancelNotifications
import com.victor.worker.location.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class LocationService: Service() {

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private lateinit var locationClient: LocationClient

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        locationClient = DefaultLocationClient(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when(intent?.action) {
            ACTION_START -> start()
            ACTION_STOP -> stop()
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notificationBuilder = NotificationUtil.getNotificationBuilder(this)

        locationClient
            .getLocationUpdates()
            .catch { e -> e.printStackTrace() }
            .onEach { location ->
                val address = LocationUtils.instance.getLocationAddress(location)
                App.get().updateLocation(address)

                NotificationUtil.sendNotification(notificationBuilder,location,address)
            }
            .launchIn(serviceScope)

        startForeground(NotificationUtil.NOTIFICATION_ID, notificationBuilder.build())
    }


    private fun stop() {
        stopForeground(true)
        stopSelf()
        cancelNotifications()
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    companion object {
        const val ACTION_START = "ACTION_START"
        const val ACTION_STOP = "ACTION_STOP"
    }
}