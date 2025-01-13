package com.victor.worker.location

interface OnLocationListener {
    fun OnLocation(address: String?,locationTime: String)
}