package id.xxx.fake.gps.presentation.service

import android.location.Location
import android.location.LocationManager
import android.location.LocationManager.GPS_PROVIDER
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlin.random.Random

class FakeLocation private constructor(
    private val locationManager: LocationManager?,
    private val callback: Callback
) {
    companion object {
        private val TAG = FakeLocation::class.java.simpleName
        var isRunning = MutableLiveData(false)

        @Volatile
        private var instance: FakeLocation? = null
        fun getInstance(locationManager: LocationManager?, callback: Callback): FakeLocation {
            instance ?: synchronized(this) {
                instance = FakeLocation(locationManager, callback)
            }
            return instance as FakeLocation
        }
    }

    private var job: Job? = null

    fun run(latitude: Double, longitude: Double) {
        if (locationManager?.isProviderEnabled(GPS_PROVIDER) != true) {
            callback.status(Status.GPS_OFF);return
        }
        if (latitude == 0.0 && longitude == 0.0) {
            callback.status(Status.ERROR_LAT_LONG);return
        }

        if (removeTestProvider() && addTestProvider()) {
            isRunning.postValue(true)
            callback.status(Status.RUNNING)

            job?.cancel()
            job = CoroutineScope(Dispatchers.Default).launch {
                while (true) {
                    val location = setLocation(latitude, longitude)
                    try {
                        locationManager.setTestProviderLocation(GPS_PROVIDER, location)
                    } catch (e: Exception) {
                        Log.e(TAG, "run: locationManager.setTestProviderLocation()")
                    }
                    delay(1000)
                }
            }
        }
    }

    private fun addTestProvider(): Boolean {
        return try {
            locationManager?.apply {
                addTestProvider(GPS_PROVIDER, true, true, false, false, true, true, true, 1, 0)
                setTestProviderEnabled(GPS_PROVIDER, true)
            }
            true
        } catch (e: RuntimeException) {
            Log.i("TAG", "addTestProvider: ${e.message}")
            false
        }
    }

    private fun removeTestProvider(): Boolean =
        try {
            locationManager?.removeTestProvider(GPS_PROVIDER)
            true
        } catch (e: Exception) {
            when (e) {
                is SecurityException -> {
                    Log.i(TAG, "removeTestProvider SecurityException: ${e.message}")
                    callback.status(Status.ERROR_SECURITY_EXCEPTION)
                    false
                }
                is IllegalArgumentException -> {
                    Log.i(TAG, "removeTestProvider IllegalArgumentException: ${e.message}")
                    true
                }
                else -> {
                    Log.i(TAG, "removeTestProvider: ${e.message}")
                    false
                }
            }
        }

    private fun setLocation(latitude: Double, longitude: Double) = Location(GPS_PROVIDER).apply {
        this.latitude = latitude
        this.longitude = longitude
        accuracy = Random.nextDouble(0.0, 1.0).toFloat()
        bearing = Random.nextDouble(0.0, 360.0).toFloat()
        altitude = Random.nextDouble(0.0, 84.0)
        speed = Random.nextDouble(0.0, 0.5).toFloat()
        time = SystemClock.elapsedRealtimeNanos()
//            time = System.currentTimeMillis(); /* FROM ANDROID UNDER 5.0 */
        elapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos()
        if (VERSION.SDK_INT > VERSION_CODES.O) {
            speedAccuracyMetersPerSecond = Random.nextDouble(0.0, 100.0).toFloat()
            bearingAccuracyDegrees = Random.nextDouble(0.0, 360.0).toFloat()
            verticalAccuracyMeters = Random.nextDouble(0.0, 100.0).toFloat()
        }
    }

    fun stop() {
        removeTestProvider()
        job?.cancel()
        isRunning.postValue(false)
        callback.status(Status.STOP)
    }

    interface Callback {
        fun status(status: Status)
    }

    enum class Status {
        RUNNING, STOP, ERROR_SECURITY_EXCEPTION, ERROR_LAT_LONG, GPS_OFF
    }
}