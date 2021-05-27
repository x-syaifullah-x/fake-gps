package id.xxx.fake.gps.presentation.ui.home

import androidx.navigation.fragment.findNavController
import id.xxx.base.presentation.binding.activity.BaseActivityWithNavigation
import id.xxx.base.presentation.binding.delegate.viewBinding
import id.xxx.fake.gps.R
import id.xxx.fake.gps.databinding.ActivityHomeBinding
import id.xxx.fake.gps.presentation.ui.home.map.Map

class HomeActivity : BaseActivityWithNavigation<ActivityHomeBinding>() {

    override val binding by viewBinding(ActivityHomeBinding::inflate)

    override fun getIdNavHost() = R.id.nav_host_main

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            Map.REQUEST_PERMISSION_LOCATION -> if (grantResults[0] == 0 && grantResults[1] == 0) recreate()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navHostFragment.findNavController().navigateUp()
    }
}