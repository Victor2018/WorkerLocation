package com.victor.worker.location

import android.app.Application

class App: Application() {
    companion object {
        private var instance : App ?= null
        public fun get() = instance!!
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}