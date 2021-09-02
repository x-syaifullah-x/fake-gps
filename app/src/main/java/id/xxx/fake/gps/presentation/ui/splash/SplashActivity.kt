package id.xxx.fake.gps.presentation.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.xxx.fake.gps.presentation.ui.home.HomeActivity
import id.xxx.module.presentation.extension.hideSystemUI
import id.xxx.module.presentation.extension.openActivityAndFinish

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openActivityAndFinish<HomeActivity>()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) hideSystemUI()
    }
}