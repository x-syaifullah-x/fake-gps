package id.xxx.fake.gps.presentation.ui.splash

import android.app.Activity
import android.os.Bundle
import id.xxx.fake.gps.presentation.ui.home.HomeActivity
import id.xxx.module.ktx.hideSystemUI
import id.xxx.module.ktx.startActivity

class StartedActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<HomeActivity> { finish() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus)
            hideSystemUI()
    }
}