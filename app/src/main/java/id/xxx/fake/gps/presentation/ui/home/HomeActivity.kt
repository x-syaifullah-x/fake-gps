package id.xxx.fake.gps.presentation.ui.home

import android.app.Activity
import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
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
import id.xxx.fake.gps.R
import id.xxx.fake.gps.databinding.ActivityMainBinding
import id.xxx.fake.gps.presentation.service.FakeLocation
import id.xxx.fake.gps.presentation.service.FakeLocationService
import id.xxx.fake.gps.presentation.ui.home.map.Map
import id.xxx.fake.gps.presentation.utils.formatDouble
import id.xxx.fake.gps.user_handle.data.repository.UserHandleRepoRepository
import id.xxx.fake.gps.user_handle.domain.UserHandleModel
import id.xxx.fake.gps.user_handle.domain.usecase.UserHandleImpl
import id.xxx.map.box.search.domain.model.PlacesModel
import id.xxx.map.box.search.presentation.ui.SearchActivity
import id.xxx.module.activity.base.BaseAppCompatActivity
import id.xxx.module.auth.model.SignModel
import id.xxx.module.intent.ktx.getParcelableExtra
import id.xxx.module.intent.ktx.getSerializableExtra
import id.xxx.module.model.sealed.Resource
import id.xxx.module.viewbinding.ktx.viewBinding
import kotlinx.coroutines.launch

class HomeActivity : BaseAppCompatActivity(),
    Map.Callback,
    GoogleMap.OnCameraMoveListener,
    GoogleMap.OnMarkerClickListener,
    View.OnClickListener {

    companion object {
        const val USER_ID_DATA_EXTRA = "USER_ID_DATA_EXTRA"
    }

    private val viewBinding by viewBinding<ActivityMainBinding>()

    private lateinit var appBarConfiguration: AppBarConfiguration
    private var location: Location? = null
    private var googleMap: GoogleMap? = null
    private var map: Map? = null

    private val navHostFragment by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val searchActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data =
                    it.data.getParcelableExtra<PlacesModel>(SearchActivity.DATA_EXTRA)
                        ?: return@registerForActivityResult
                addSingleMaker(LatLng(data.latitude, data.longitude))
            }
        }

    private val userRepo by lazy {
        UserHandleImpl.getInstance(UserHandleRepoRepository.getInstance(this))
    }

    private val authActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            val data = activityResult.data
            val result =
                data.getSerializableExtra<SignModel>(id.xxx.module.auth.AuthActivity.RESULT_USER)
            if (result != null) {
                lifecycleScope.launch {
                    userRepo.signIn(model = UserHandleModel(uid = result.uid))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(viewBinding.root)

        viewBinding.appBarMain.appBar.outlineProvider = null
        setSupportActionBar(viewBinding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = viewBinding.drawerLayout
        val navView: NavigationView = viewBinding.navView
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_history, R.id.nav_favorite), drawerLayout
        )
        setupActionBarWithNavController(navHostFragment.navController, appBarConfiguration)
        navView.setupWithNavController(navHostFragment.navController)

        val smf = (supportFragmentManager.findFragmentById(R.id.map_view) as SupportMapFragment)
        map = Map(smf, this)

        viewBinding.appBarMain.btnStopFake.setOnClickListener(this)
        viewBinding.appBarMain.btnStartFake.setOnClickListener(this)
        viewBinding.appBarMain.toolbar.setOnClickListener(this)
        viewBinding.btnSignIn.setOnClickListener(this)
        viewBinding.btnSignOut.setOnClickListener(this)
        viewBinding.appBarMain.aciMyPosition.setOnClickListener(this)

        val navHostFragmentLayoutParams: CoordinatorLayout.LayoutParams =
            viewBinding.appBarMain.navHostFragment.layoutParams as CoordinatorLayout.LayoutParams
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, arguments ->
            val isHomePage = destination.id == R.id.nav_home
            viewBinding.appBarMain.mapView.isVisible = isHomePage
            viewBinding.appBarMain.navHostFragment.isVisible = !isHomePage
            if (arguments != null) {
                val lat = arguments.getString("latitude")?.toDouble()
                val lon = arguments.getString("longitude")?.toDouble()
                if (lat != null && lon != null && lat != 0.0 && lon != 0.0) {
                    addSingleMaker(LatLng(lat, lon))
                }
            }

            viewBinding.appBarMain.aciMyPosition.isVisible = isHomePage
            navHostFragmentLayoutParams.behavior =
                if (isHomePage) null else AppBarLayout.ScrollingViewBehavior()
            viewBinding.appBarMain.navHostFragment.requestLayout()
        }

        FakeLocation.isRunning.observe(this) {
            viewBinding.appBarMain.btnStopFake.visibility = if (it) View.VISIBLE else View.GONE
        }

        observerUser()
    }

    private fun observerUser() {
        userRepo
            .currentUser()
            .asLiveData()
            .observe(this) { it: Resource<UserHandleModel> ->
                when (it) {
                    is Resource.Loading -> {
                        viewBinding.btnSignOut.isVisible = false
                        viewBinding.btnSignIn.isVisible = false
                    }

                    is Resource.Success -> {
                        viewBinding.btnSignOut.isVisible = true
                        viewBinding.btnSignIn.isVisible = false
                    }

                    is Resource.Empty -> {
                        viewBinding.btnSignOut.isVisible = false
                        viewBinding.btnSignIn.isVisible = true
                    }

                    is Resource.Error -> {
                        viewBinding.btnSignOut.isVisible = false
                        viewBinding.btnSignIn.isVisible = true
                    }
                }
            }

        //        val navHeaderMainBinding = NavHeaderMainBinding.bind(binding.navView.getHeaderView(0))

//        val regex =
//            Pattern.compile("@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).toRegex()

        //        if (currentUserObserver == null)
//            currentUserObserver = Observer<Resource<UserModel>> {
//                it.whenNoReturn(
//                    blockLoading = {
//                        binding.progressCircular.visibility = View.VISIBLE
//                        binding.btnSignIn.visibility = View.GONE
//                        binding.btnSignOut.visibility = View.GONE
//                    },
//                    blockSuccess = { userModel ->
//                        val name = userModel.email?.replace(regex, "")
//                        navHeaderMainBinding.navHeaderTextName.text = name
//                        navHeaderMainBinding.navHeaderTextEmail.text = userModel.email
//                        intent.putExtra(USER_ID_DATA_EXTRA, userModel.id)
//
//                        binding.progressCircular.visibility = View.GONE
//                        binding.btnSignOut.isVisible = true
//                        binding.btnSignIn.isVisible = false
//                    },
//                    blockEmpty = {
//                        binding.progressCircular.visibility = View.GONE
//                        binding.btnSignOut.visibility = View.GONE
//                        binding.btnSignIn.visibility = View.VISIBLE
//                        intent.removeExtra(USER_ID_DATA_EXTRA)
//                        navHeaderMainBinding.navHeaderTextName.text =
//                            getString(R.string.nav_header_title)
//                        navHeaderMainBinding.navHeaderTextEmail.text =
//                            getString(R.string.nav_header_subtitle)
//                    },
//                    blockError = { _, e ->
//                        Toast.makeText(this, e.localizedMessage, Toast.LENGTH_LONG).show()
//                        binding.progressCircular.visibility = View.GONE
//                        binding.btnSignOut.visibility = View.GONE
//                        binding.btnSignIn.visibility = View.GONE
//                        binding.btnTryAgain.visibility = View.VISIBLE
//                        binding.btnTryAgain.setOnClickListener {
//                            binding.btnTryAgain.visibility = View.GONE
//                            binding.progressCircular.visibility = View.VISIBLE
//                            if (currentUserObserver != null) {
//                                homeViewModel.currentUser.removeObserver(
//                                    currentUserObserver ?: return@setOnClickListener
//                                )
//                                homeViewModel.currentUser.observe(
//                                    this, currentUserObserver ?: return@setOnClickListener
//                                )
//                            }
//                        }
//                    }
//                )
//            }
//        homeViewModel.currentUser.observe(this, currentUserObserver ?: return)
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
                    viewBinding.appBarMain.btnStartFake.visibility = View.VISIBLE
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
        viewBinding.appBarMain.btnStartFake.visibility = View.GONE
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

    override fun onMarkerClick(p0: Marker): Boolean {
        removeSingleMarker()
        return true
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            viewBinding.appBarMain.btnStartFake.id -> markerPosition?.apply {
                fakeStart(position.latitude, position.longitude)
                removeSingleMarker()
            }

            viewBinding.appBarMain.btnStopFake.id ->
                stopService(Intent(baseContext, FakeLocationService::class.java))

            viewBinding.appBarMain.toolbar.id ->
                searchActivityResultLauncher.launch(Intent(v.context, SearchActivity::class.java))

            viewBinding.btnSignIn.id -> {
                authActivityResultLauncher.launch(
                    Intent(this, id.xxx.module.auth.AuthActivity::class.java)
                )
            }

            viewBinding.btnSignOut.id -> {
                lifecycleScope.launch { userRepo.signOut() }
//                if (signOutObserver == null)
//                    signOutObserver = Observer {
//                        it.whenNoReturn(
//                            blockLoading = {
//                                binding.progressCircular.visibility = View.VISIBLE
//                                binding.btnSignOut.visibility = View.GONE
//                            },
//                            blockSuccess = {
//                                homeViewModel.signOut()
//                                    .removeObserver(signOutObserver ?: return@whenNoReturn)
//                            },
//                            blockError = { _, _ ->
//                                binding.progressCircular.visibility = View.GONE
//                                binding.btnSignOut.visibility = View.VISIBLE
//                                homeViewModel.signOut()
//                                    .removeObserver(signOutObserver ?: return@whenNoReturn)
//                            }
//                        )
//                    }
//                homeViewModel.signOut().observe(this, signOutObserver ?: return)
            }

            viewBinding.appBarMain.aciMyPosition.id ->
                googleMap?.apply { map?.enableMyPosition(this@HomeActivity, this) }
        }
    }

    private fun removeSingleMarker() {
        viewBinding.appBarMain.btnStartFake.visibility = View.GONE
        googleMap?.clear()
        markerPosition = null
        markerOptions = null
    }
}