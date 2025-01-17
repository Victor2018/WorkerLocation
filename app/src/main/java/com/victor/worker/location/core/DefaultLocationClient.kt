package com.victor.worker.location.core
import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class DefaultLocationClient(
    private val context: Context,
): LocationClient {

    companion object {
        private const val TAG = "DefaultLocationClient"
        const val LOCATION_UPDATE_INTERVAL = 3 * 60 * 1000L//每3分钟刷新一次位置
    }
    @SuppressLint("MissingPermission")
    override fun getLocationUpdates(): Flow<Location> {
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
                locationManager.requestLocationUpdates(provider, LOCATION_UPDATE_INTERVAL, 0f
                ) { location ->
                    Log.d(TAG,"longitude = ${location.longitude}")
                    Log.d(TAG,"latitude = ${location.latitude}")

                    launch {
                        send(location)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            awaitClose {
            }
        }
    }
}