package id.xxx.fake.gps.history.presentation

import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import id.xxx.fake.gps.history.presentation.databinding.ActivityHistoryBinding
import id.xxx.module.presentation.binding.activity.BaseActivity
import id.xxx.module.presentation.binding.ktx.viewBinding
import kotlin.random.Random

class HistoryActivity : BaseActivity<ActivityHistoryBinding>() {

    override val binding by viewBinding(ActivityHistoryBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(findNavController(R.id.nav_host_history))
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || findNavController(R.id.nav_host_history).navigateUp()
    }

    companion object {
        val REQUEST_CODE by lazy { Random.nextInt(100, 1000) }
    }
}