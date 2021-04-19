package id.xxx.fake.gps.presentation.utils

import android.app.Activity
import android.content.IntentSender.SendIntentException
import android.util.Log
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

object Request {
    private val TAG = Request::class.java.simpleName
    private val locationRequest = LocationRequest.create()

    fun enableGps(activity: Activity?, requestCode: Int) {
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val client = LocationServices.getSettingsClient(activity!!)
        val task = client.checkLocationSettings(builder.build())
        task.addOnFailureListener(activity) { e: Exception? ->
            if (e is ResolvableApiException) {
                try {
                    e.startResolutionForResult(activity, requestCode)
                } catch (sendEx: SendIntentException) {
                    Log.e(TAG, "gps: ", sendEx)
                }
            }
        }
    }
}