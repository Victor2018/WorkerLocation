package com.victor.worker.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date

class LocationUtils: LocationListener {
    private val TAG = "LocationUtils"
    private object Holder { val instance = LocationUtils()}
    companion object {
        val instance: LocationUtils by lazy { LocationUtils.Holder.instance }
    }
    private val simpleDateFormat by lazy {
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    }
    private var mOnLocationListener: OnLocationListener? = null

    @SuppressLint("MissingPermission")
    fun getLocation(listener: OnLocationListener) {
        try {
            mOnLocationListener = listener
            val mLocationManager = App.get().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.isCostAllowed = true
            val provider = mLocationManager.getBestProvider(criteria,true) ?: ""

            Log.d(TAG,"provider: $provider")

//        if(mLocationOption.isLastKnownLocation){
//            Log.d(TAG,"Get last known location.")
//            val location = mLocationManager.getLastKnownLocation(provider)
//            if(location != null){
//                mOnLocationListener?.onLocationChanged(location)
//                if(mLocationOption.isOnceLocation){//如果只定位一次，则直接拦截
//                    return
//                }
//            }
//        }
        //监听位置更新
            mLocationManager.requestLocationUpdates(provider, 10000, 0f, this)
        } catch (e: Exception){
            e.printStackTrace()
            mOnLocationListener?.OnLocation("",simpleDateFormat.format(System.currentTimeMillis()))
        }
    }

    override fun onLocationChanged(location: Location) {
        val geocoder = Geocoder(App.get())
        //根据坐标经纬度获取位置地址信息（WGS-84坐标系）
        val list = geocoder.getFromLocation(location.latitude,location.longitude,1)

        val address = list?.getOrNull(0)?.getAddressLine(0)
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        mOnLocationListener?.OnLocation(address,simpleDateFormat.format(Date(location.time)))
    }

    fun getLocationAddress(location: Location): String {
        val geocoder = Geocoder(App.get())
        //根据坐标经纬度获取位置地址信息（WGS-84坐标系）
        val list = geocoder.getFromLocation(location.latitude,location.longitude,1)
        val address = list?.firstOrNull()?.getAddressLine(0)
        return address ?: ""
    }
}