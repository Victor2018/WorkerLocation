package com.victor.worker.location

import android.app.Application
import androidx.lifecycle.MutableLiveData

class App: Application() {

    val locationLiveData = MutableLiveData<String>()

    companion object {
        private var instance : App ?= null
        public fun get() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        NotificationUtil.createNotificationChannel()
    }

    fun updateLocation(location: String) {
        locationLiveData.postValue(location)
    }
}