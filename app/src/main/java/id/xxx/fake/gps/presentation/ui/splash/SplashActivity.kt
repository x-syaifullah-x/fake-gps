package id.xxx.fake.gps.presentation.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.xxx.fake.gps.presentation.ui.home.HomeActivity
import id.xxx.module.presentation.base.ktx.hideSystemUI
import id.xxx.module.presentation.base.ktx.startActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startActivity<HomeActivity> { finish() }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) hideSystemUI()
    }
}