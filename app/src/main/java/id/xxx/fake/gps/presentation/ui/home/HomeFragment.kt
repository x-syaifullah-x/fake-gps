package id.xxx.fake.gps.presentation.ui.home

import android.content.Intent
import android.location.Location
import android.location.LocationManager.GPS_PROVIDER
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import id.xxx.auth.presentation.ui.AuthActivity
import id.xxx.fake.gps.R
import id.xxx.fake.gps.databinding.FragmentHomeBinding
import id.xxx.fake.gps.history.presentation.HistoryActivity
import id.xxx.fake.gps.presentation.service.FakeLocation
import id.xxx.fake.gps.presentation.service.FakeLocationService
import id.xxx.fake.gps.presentation.ui.home.map.Map
import id.xxx.fake.gps.presentation.utils.formatDouble
import id.xxx.map.box.search.domain.model.PlacesModel
import id.xxx.map.box.search.presentation.ui.SearchActivity
import id.xxx.module.domain.model.Resource
import id.xxx.module.domain.model.Resource.Companion.whenNoReturn
import id.xxx.module.presentation.binding.delegate.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment(R.layout.fragment_home),
    Map.Callback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnMarkerClickListener,
    View.OnClickListener {

    companion object {
        const val USER_ID_DATA_EXTRA = "USER_ID_DATA_EXTRA"
        private const val SIGN_IN_CODE = 322
        private const val SEARCH_REQUEST_CODE = 262
    }

    private val homeViewModel by viewModel<HomeViewModel>()

    private val binding by viewBinding<FragmentHomeBinding>()

    private lateinit var map: Map

    private var googleMap: GoogleMap? = null
    private var location: Location = Location(GPS_PROVIDER)
    private var markerPosition: Marker? = null
    private var markerOptions: MarkerOptions? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val smf = (childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment)
        map = Map(smf, this).apply { binding.onClick = this@HomeFragment }

        FakeLocation.isRunning.observe(viewLifecycleOwner, {
            binding.btnStopFake.visibility = if (it) VISIBLE else GONE
        })

        homeViewModel.currentUser.observe(viewLifecycleOwner) {
            it.whenNoReturn(
                blockSuccess = { userModel ->
                    requireActivity().intent.putExtra(
                        USER_ID_DATA_EXTRA,
                        userModel.id
                    )
                }
            )
            binding.btnLogout.isVisible = (it is Resource.Success)
            binding.btnSign.isVisible = !binding.btnLogout.isVisible
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        when (requestCode) {
            Map.REQUEST_CODE_ENABLE_GPS -> googleMap?.apply {
                map.enableMyPosition(requireActivity(), this)
            }
            SEARCH_REQUEST_CODE -> {
                intent?.apply {
                    val data = getParcelableExtra<PlacesModel>(SearchActivity.DATA_EXTRA) ?: return
                    addSingleMaker(LatLng(data.latitude, data.longitude))
                }
            }
            HistoryActivity.REQUEST_CODE -> {
                intent?.apply {
                    LatLng(
                        getDoubleExtra("latitude", 0.0), getDoubleExtra("longitude", 0.0)
                    ).apply { addSingleMaker(this) }
                }
            }
        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        fakeStart(latLng.latitude, latLng.longitude)
    }

    override fun onMapClick(latLng: LatLng) {
        addSingleMaker(latLng)
    }

    override fun onCameraMove() {
        googleMap?.apply {
            val cameraPosition = cameraPosition
            cameraPosition.apply {
                val a =
                        formatDouble(target.latitude) == formatDouble(location.latitude)
                val b =
                        formatDouble(target.longitude) == formatDouble(location.longitude)
                binding.aciMyPosition.apply { visibility = if (a && b) GONE else VISIBLE }
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        moveCamera(location.latitude, location.longitude)
    }

    override fun onMarkerClick(marker: Marker?): Boolean {
        removeSingleMarker()
        return true
    }

    override fun onCameraIdle() {
        googleMap?.let { googleMap ->
            markerOptions?.let { markerOptions ->
                markerPosition = googleMap.addMarker(markerOptions).apply {
                    binding.btnStartFake.visibility = VISIBLE
                    showInfoWindow()
                    googleMap.setOnMarkerClickListener(this@HomeFragment)
//                    googleMap.setOnInfoWindowClickListener(this@HomeFragment)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_logout -> {
                homeViewModel.signOut().observe(viewLifecycleOwner) {
                    it.whenNoReturn(
                        blockSuccess = {
                            requireActivity().intent.removeExtra(USER_ID_DATA_EXTRA)
                            binding.btnLogout.isVisible = false
                            binding.btnSign.isVisible = true
                        }
                    )
                }
            }

            R.id.btn_sign -> {
                startActivityForResult(
                    Intent(requireContext(), AuthActivity::class.java),
                    SIGN_IN_CODE
                )
            }

            R.id.toolbar -> {
                val intent = Intent(requireContext(), SearchActivity::class.java)
                requireActivity().startActivityForResult(intent, SEARCH_REQUEST_CODE)
            }
            R.id.aci_my_position -> googleMap?.apply {
                map.enableMyPosition(requireActivity(), this)
            }
            R.id.btn_move_to_history -> {
                val intent = Intent(
                    requireActivity(),
                    HistoryActivity::class.java
                ).apply {
                    putExtra(
                        USER_ID_DATA_EXTRA,
                        requireActivity().intent.getStringExtra(USER_ID_DATA_EXTRA)
                    )
                }
                requireActivity().startActivityForResult(
                    intent,
                    HistoryActivity.REQUEST_CODE
                )
            }
            R.id.btn_start_fake -> {
                markerPosition?.apply {
                    fakeStart(position.latitude, position.longitude)
                    removeSingleMarker()
                }
            }
            R.id.btn_stop_fake -> {
                requireActivity().stopService(Intent(context, FakeLocationService::class.java))
            }
        }
    }

    private fun fakeStart(latitude: Double, longitude: Double) {
        Intent(requireContext(), FakeLocationService::class.java).apply {
            putExtra(
                USER_ID_DATA_EXTRA,
                requireActivity().intent.getStringExtra(USER_ID_DATA_EXTRA)
            )
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            requireActivity().startService(this)
        }
    }

    private fun moveCamera(latitude: Double, longitude: Double) {
        googleMap?.apply {
            animateCamera(
                    CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), cameraPosition.zoom)
            )
        }
    }

    private fun addSingleMaker(latLng: LatLng) {
        binding.btnStartFake.visibility = GONE
        googleMap?.clear()
        moveCamera(latLng.latitude, latLng.longitude)
        markerOptions = MarkerOptions().title("show location").position(latLng)
    }

    private fun removeSingleMarker() {
        binding.btnStartFake.visibility = GONE
        googleMap?.clear()
        markerPosition = null
        markerOptions = null
    }
}