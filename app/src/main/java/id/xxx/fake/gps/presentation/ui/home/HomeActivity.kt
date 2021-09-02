package id.xxx.fake.gps.presentation.ui.home

import androidx.navigation.findNavController
import id.xxx.fake.gps.R
import id.xxx.fake.gps.databinding.ActivityHomeBinding
import id.xxx.fake.gps.presentation.ui.home.map.Map
import id.xxx.module.presentation.binding.activity.BaseActivity
import id.xxx.module.presentation.binding.delegate.viewBinding

class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override val binding by viewBinding(ActivityHomeBinding::inflate)

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
        return super.onSupportNavigateUp() || findNavController(R.id.nav_host_main).navigateUp()
    }
}