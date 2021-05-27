package id.xxx.fake.gps.presentation.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.xxx.base.presentation.extension.hideSystemUI
import id.xxx.base.presentation.extension.openActivityAndFinish
import id.xxx.fake.gps.presentation.ui.home.HomeActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        openActivityAndFinish<HomeActivity>()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) hideSystemUI()
    }
}