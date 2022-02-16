package id.xxx.fake.gps.presentation.ui.home

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import id.xxx.auth.domain.model.UserModel
import id.xxx.auth.presentation.ui.AuthActivity
import id.xxx.fake.gps.R
import id.xxx.fake.gps.databinding.ActivityMainBinding
import id.xxx.fake.gps.databinding.NavHeaderMainBinding
import id.xxx.fake.gps.presentation.service.FakeLocation
import id.xxx.fake.gps.presentation.service.FakeLocationService
import id.xxx.fake.gps.presentation.ui.home.map.Map
import id.xxx.fake.gps.presentation.utils.formatDouble
import id.xxx.map.box.search.domain.model.PlacesModel
import id.xxx.map.box.search.presentation.ui.SearchActivity
import id.xxx.module.model.sealed.Resource
import id.xxx.module.model.sealed.Resource.Companion.whenNoReturn
import id.xxx.module.presentation.binding.activity.BaseActivity
import id.xxx.module.view.binding.ktx.viewBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.regex.Pattern

class HomeActivity : BaseActivity<ActivityMainBinding>(),
    Map.Callback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnMarkerClickListener,
    View.OnClickListener {

    companion object {
        const val USER_ID_DATA_EXTRA = "USER_ID_DATA_EXTRA"
    }

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var location: Location? = null
    private var googleMap: GoogleMap? = null
    private var map: Map? = null
    private var signOutObserver: Observer<Resource<Boolean>>? = null
    private var currentUserObserver: Observer<Resource<UserModel>>? = null

    private val homeViewModel by viewModel<HomeViewModel>()

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val searchActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data =
                    it.data?.getParcelableExtra<PlacesModel>(SearchActivity.DATA_EXTRA)
                        ?: return@registerForActivityResult
                addSingleMaker(LatLng(data.latitude, data.longitude))
            }
        }

    private val authActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.appBarMain.appBar.outlineProvider = null
        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_history, R.id.nav_favorite), drawerLayout
        )
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        navView.setupWithNavController(navHostFragment.navController)

        val smf = (supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment)
        map = Map(smf, this)

        binding.appBarMain.btnStopFake.setOnClickListener(this)
        binding.appBarMain.btnStartFake.setOnClickListener(this)
        binding.appBarMain.toolbar.setOnClickListener(this)
        binding.btnSignIn.setOnClickListener(this)
        binding.btnSignOut.setOnClickListener(this)
        binding.appBarMain.aciMyPosition.setOnClickListener(this)

        val navHostFragmentLayoutParams: CoordinatorLayout.LayoutParams =
            binding.appBarMain.navHostFragment.layoutParams as CoordinatorLayout.LayoutParams
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, arguments ->
            val isHomePage = destination.id == R.id.nav_home
            binding.appBarMain.mapView.isVisible = isHomePage
            binding.appBarMain.navHostFragment.isVisible = !isHomePage
            if (arguments != null) {
                val lat = arguments.getString("latitude")?.toDouble()
                val lon = arguments.getString("longitude")?.toDouble()
                if (lat != null && lon != null && lat != 0.0 && lon != 0.0) {
                    addSingleMaker(LatLng(lat, lon))
                }
            }

            binding.appBarMain.aciMyPosition.isVisible = isHomePage
            navHostFragmentLayoutParams.behavior =
                if (isHomePage) null else AppBarLayout.ScrollingViewBehavior()
            binding.appBarMain.navHostFragment.requestLayout()
        }

        FakeLocation.isRunning.observe(this) {
            binding.appBarMain.btnStopFake.visibility = if (it) View.VISIBLE else View.GONE
        }

        val navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

        val regex =
            Pattern.compile("@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).toRegex()

        if (currentUserObserver == null)
            currentUserObserver = Observer<Resource<UserModel>> {
                it.whenNoReturn(
                    blockLoading = {
                        binding.progressCircular.visibility = View.VISIBLE
                        binding.btnSignIn.visibility = View.GONE
                        binding.btnSignOut.visibility = View.GONE
                    },
                    blockSuccess = { userModel ->
                        val name = userModel.email?.replace(regex, "")
                        navHeaderMainBinding.navHeaderTextName.text = name
                        navHeaderMainBinding.navHeaderTextEmail.text = userModel.email
                        intent.putExtra(USER_ID_DATA_EXTRA, userModel.id)

                        binding.progressCircular.visibility = View.GONE
                        binding.btnSignOut.isVisible = true
                        binding.btnSignIn.isVisible = false
                    },
                    blockEmpty = {
                        binding.progressCircular.visibility = View.GONE
                        binding.btnSignOut.visibility = View.GONE
                        binding.btnSignIn.visibility = View.VISIBLE
                        intent.removeExtra(USER_ID_DATA_EXTRA)
                        navHeaderMainBinding.navHeaderTextName.text =
                            getString(R.string.nav_header_title)
                        navHeaderMainBinding.navHeaderTextEmail.text =
                            getString(R.string.nav_header_subtitle)
                    },
                    blockError = { _, e ->
                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
                        binding.progressCircular.visibility = View.GONE
                        binding.btnSignOut.visibility = View.GONE
                        binding.btnSignIn.visibility = View.GONE
                        binding.btnTryAgain.visibility = View.VISIBLE
                        binding.btnTryAgain.setOnClickListener {
                            binding.btnTryAgain.visibility = View.GONE
                            binding.progressCircular.visibility = View.VISIBLE
                            if (currentUserObserver != null) {
                                homeViewModel.currentUser.removeObserver(
                                    currentUserObserver ?: return@setOnClickListener
                                )
                                homeViewModel.currentUser.observe(
                                    this, currentUserObserver ?: return@setOnClickListener
                                )
                            }
                        }
                    }
                )
            }
        homeViewModel.currentUser.observe(this, currentUserObserver ?: return)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Map.REQUEST_PERMISSION_LOCATION -> if (grantResults[0] == 0 && grantResults[1] == 0) recreate()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
//        return super.onSupportNavigateUp() || navHostFragment.navController.navigateUp()
        return navHostFragment.navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
    }

    override fun onLocationChanged(location: Location) {
        this.location = location
        moveCamera(location.latitude, location.longitude)
    }

    private var markerOptions: MarkerOptions? = null
    private var markerPosition: Marker? = null

    override fun onCameraIdle() {
        googleMap?.let { googleMap ->
            markerOptions?.let { markerOptions ->
                markerPosition = googleMap.addMarker(markerOptions)?.apply {
                    binding.appBarMain.btnStartFake.visibility = View.VISIBLE
                    showInfoWindow()
                    googleMap.setOnMarkerClickListener(this@HomeActivity)
//                    googleMap.setOnInfoWindowClickListener(this@HomeFragment)
                }
            }
        }
    }

    private fun fakeStart(latitude: Double, longitude: Double) {
        Intent(baseContext, FakeLocationService::class.java).apply {
            putExtra(USER_ID_DATA_EXTRA, intent.getStringExtra(USER_ID_DATA_EXTRA))
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            startService(this)
        }
    }

    private fun addSingleMaker(latLng: LatLng) {
        binding.appBarMain.btnStartFake.visibility = View.GONE
        googleMap?.clear()
        moveCamera(latLng.latitude, latLng.longitude)
        markerOptions = MarkerOptions().title("show location").position(latLng)
    }

    override fun onMapClick(latLng: LatLng) {
        addSingleMaker(latLng)
    }

    override fun onMapLongClick(latLng: LatLng) {
        fakeStart(latLng.latitude, latLng.longitude)
    }

    override fun onCameraMove() {
        googleMap?.apply {
            val cameraPosition = cameraPosition
            cameraPosition.apply {
                val a = formatDouble(target.latitude) == formatDouble(location?.latitude ?: 0.0)
                val b = formatDouble(target.longitude) == formatDouble(location?.longitude ?: 0.0)
                findViewById<FloatingActionButton>(R.id.aci_my_position)
                    ?.apply { visibility = if (a && b) View.GONE else View.VISIBLE }
            }
        }
    }

    private fun moveCamera(latitude: Double, longitude: Double) {
        googleMap?.apply {
            animateCamera(
                CameraUpdateFactory.newLatLngZoom(LatLng(latitude, longitude), cameraPosition.zoom)
            )
        }
    }

    private fun removeSingleMarker() {
        binding.appBarMain.btnStartFake.visibility = View.GONE
        googleMap?.clear()
        markerPosition = null
        markerOptions = null
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        removeSingleMarker()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.appBarMain.btnStartFake.id -> markerPosition?.apply {
                fakeStart(position.latitude, position.longitude)
                removeSingleMarker()
            }
            binding.appBarMain.btnStopFake.id ->
                stopService(Intent(baseContext, FakeLocationService::class.java))
            binding.appBarMain.toolbar.id ->
                searchActivityResultLauncher.launch(Intent(v.context, SearchActivity::class.java))
            binding.btnSignIn.id ->
                authActivityResultLauncher.launch(Intent(this, AuthActivity::class.java))
            binding.btnSignOut.id -> {
                if (signOutObserver == null)
                    signOutObserver = Observer<Resource<Boolean>> {
                        it.whenNoReturn(
                            blockLoading = {
                                binding.progressCircular.visibility = View.VISIBLE
                                binding.btnSignOut.visibility = View.GONE
                            },
                            blockSuccess = {
                                homeViewModel.signOut()
                                    .removeObserver(signOutObserver ?: return@whenNoReturn)
                            },
                            blockError = { _, _ ->
                                binding.progressCircular.visibility = View.GONE
                                binding.btnSignOut.visibility = View.VISIBLE
                                homeViewModel.signOut()
                                    .removeObserver(signOutObserver ?: return@whenNoReturn)
                            }
                        )
                    }
                homeViewModel.signOut().observe(this, signOutObserver ?: return)
            }
            binding.appBarMain.aciMyPosition.id ->
                googleMap?.apply { map?.enableMyPosition(this@HomeActivity, this) }
        }
    }
}