package id.xxx.fake.gps.presentation.ui.home.map

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationManager.NETWORK_PROVIDER
import android.location.LocationManager.PASSIVE_PROVIDER
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.maps.CameraUpdateFactory.newLatLngZoom
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import id.xxx.fake.gps.presentation.utils.Request
import id.xxx.fake.gps.presentation.utils.generateInt

class Map(supportMapFragment: SupportMapFragment, private val callback: Callback) :
    LocationListener,
    OnMapReadyCallback,
    GoogleMap.OnCameraIdleListener,
    GoogleMap.OnMapLongClickListener,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnMapClickListener,
    LifecycleObserver {

    init {

        supportMapFragment.getMapAsync(this)
        supportMapFragment.lifecycle.addObserver(this)
    }

    private lateinit var googleMap: GoogleMap

    private val context = supportMapFragment.requireContext()
    private var locationManager =
        ContextCompat.getSystemService(context, LocationManager::class.java)

    override fun onMapReady(gm: GoogleMap) {
        gm.apply {
            googleMap = gm
            uiSettings.apply {
                isMyLocationButtonEnabled = false
                isZoomControlsEnabled =
                    !context.packageManager.hasSystemFeature("android.hardware.touchscreen")
                isCompassEnabled = false
            }

            getSavedCameraPosition().let {
                moveCamera(
                    newLatLngZoom(LatLng(it.target.latitude, it.target.longitude), CAMERA_ZOOM)
                )
            }

            enableMyPosition((context as Activity), gm)
            setOnCameraIdleListener(this@Map)
            setOnMapClickListener(this@Map)
            setOnMapLongClickListener(this@Map)
            setOnCameraMoveListener(this@Map)
            callback.onMapReady(gm)
        }
    }

    override fun onLocationChanged(location: Location) {
        putSaveCameraPosition(googleMap)
        callback.onLocationChanged(location)
    }

    override fun onCameraIdle() {
        putSaveCameraPosition(googleMap)
        callback.onCameraIdle()
    }

    override fun onCameraMove() {
        callback.onCameraMove()
    }

    override fun onMapClick(latLng: LatLng) {
        latLng.let { callback.onMapClick(it) }
    }

    override fun onMapLongClick(latLng: LatLng) {
        latLng.let { callback.onMapLongClick(latLng) }
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}

    override fun onProviderDisabled(provider: String) {}

    override fun onProviderEnabled(provider: String) {}

    fun enableMyPosition(activity: Activity, gm: GoogleMap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val afl = context.checkSelfPermission(ACCESS_FINE_LOCATION)
            val acl = context.checkSelfPermission(ACCESS_COARSE_LOCATION)
            if (afl != PERMISSION_GRANTED && acl != PERMISSION_GRANTED) {
                val permission = arrayOf(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                activity.requestPermissions(permission, REQUEST_PERMISSION_LOCATION)
                return
            }
        }

        val baseProvider = locationManager?.getBestProvider(Criteria(), true)
        if (baseProvider == null || baseProvider == PASSIVE_PROVIDER || baseProvider == NETWORK_PROVIDER) {
            Request.enableGps(activity, REQUEST_CODE_ENABLE_GPS); return
        }
        if (!gm.isMyLocationEnabled) gm.isMyLocationEnabled = true
        locationManager?.requestLocationUpdates(
            baseProvider, 0, 500.0f, this
        )
    }

    private fun putSaveCameraPosition(googleMap: GoogleMap) {
        val cameraPosition = googleMap.cameraPosition
        val latitude = java.lang.Double.doubleToRawLongBits(cameraPosition.target.latitude)
        val longitude = java.lang.Double.doubleToRawLongBits(cameraPosition.target.longitude)
        context.getSharedPreferences(STAT_CAMERA_POSITION, MODE_PRIVATE).edit {
            putLong(LATITUDE, latitude)
            putLong(LONGITUDE, longitude)
            putFloat(ZOOM, cameraPosition.zoom)
            putFloat(BEARING, cameraPosition.bearing)
            putFloat(TILT, cameraPosition.tilt)
        }
    }

    private fun getSavedCameraPosition(): CameraPosition {
        val mapStatePrefs = context.getSharedPreferences(STAT_CAMERA_POSITION, MODE_PRIVATE)
        val latitude = java.lang.Double.longBitsToDouble(mapStatePrefs.getLong(LATITUDE, 0))
        val longitude = java.lang.Double.longBitsToDouble(mapStatePrefs.getLong(LONGITUDE, 0))
        val target = LatLng(latitude, longitude)
        val zoom: Float = mapStatePrefs.getFloat(ZOOM, 0f)
        val bearing: Float = mapStatePrefs.getFloat(BEARING, 0f)
        val tilt: Float = mapStatePrefs.getFloat(TILT, 0f)
        return CameraPosition(target, zoom, tilt, bearing)
    }

    companion object {
        private val random: () -> Int = { generateInt() }
        val REQUEST_PERMISSION_LOCATION by lazy { random() }
        val REQUEST_CODE_ENABLE_GPS by lazy { random() }

        private const val CAMERA_ZOOM = 16f

        private const val STAT_CAMERA_POSITION = "stat_camera_position"
        private const val LATITUDE = "LATITUDE"
        private const val LONGITUDE = "LONGITUDE"
        private const val ZOOM = "ZOOM"
        private const val BEARING = "BEARING"
        private const val TILT = "TILT"
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun removeLocationUpdate() {
        locationManager?.removeUpdates(this)
    }

    interface Callback {
        fun onMapReady(googleMap: GoogleMap)
        fun onLocationChanged(location: Location)
        fun onCameraIdle()
        fun onMapClick(latLng: LatLng)
        fun onMapLongClick(latLng: LatLng)
        fun onCameraMove()
    }
}