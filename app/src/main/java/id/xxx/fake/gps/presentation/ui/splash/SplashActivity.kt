package id.xxx.fake.gps.presentation.ui.splash

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import id.xxx.auth.domain.user.usecase.IInteractor
import id.xxx.base.presentation.extension.hideSystemUI
import id.xxx.base.presentation.extension.openActivityAndFinish
import id.xxx.fake.gps.presentation.ui.home.HomeActivity
import org.koin.android.ext.android.inject

class SplashActivity : AppCompatActivity() {

    private val interactor by inject<IInteractor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        lifecycleScope.launchWhenCreated {
//            val result = interactor.currentUser().drop(1).first()
//            if (result is Resource.Success) {
//                if (result.data.isVerify) {
//                    openActivityAndFinish<HomeActivity>()
//                } else {
//                    openActivityAndFinish<AuthActivity> { putAuthDestination(HomeActivity::class) }
//                }
//            } else {
//                openActivityAndFinish<AuthActivity> { putAuthDestination(HomeActivity::class) }
//            }
//        }

        openActivityAndFinish<HomeActivity>()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) hideSystemUI()
    }
}