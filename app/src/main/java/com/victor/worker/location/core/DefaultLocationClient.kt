package com.victor.worker.location.core
import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
): LocationClient {

    private val TAG = "DefaultLocationClient"
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(interval: Long): Flow<Location> {
        return callbackFlow {
            val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
            val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
            if(!isGpsEnabled && !isNetworkEnabled) {
                throw LocationClient.LocationException("GPS is disabled")
            }

            val criteria = Criteria()
            criteria.isCostAllowed = true
            val provider = locationManager.getBestProvider(criteria,true) ?: ""
            Log.d(TAG,"provider: $provider")

            try {
                //监听位置更新
                locationManager.requestLocationUpdates(provider, 10000, 0f
                ) { location -> launch { send(location) } }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}