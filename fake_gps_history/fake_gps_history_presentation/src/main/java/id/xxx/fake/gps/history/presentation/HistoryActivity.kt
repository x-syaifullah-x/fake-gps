package id.xxx.fake.gps.history.presentation

import android.os.Bundle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import id.xxx.base.presentation.binding.activity.BaseActivityWithNavigation
import id.xxx.base.presentation.binding.delegate.viewBinding
import id.xxx.fake.gps.history.presentation.databinding.ActivityHistoryBinding
import kotlin.random.Random

class HistoryActivity : BaseActivityWithNavigation<ActivityHistoryBinding>() {

    override val binding by viewBinding(ActivityHistoryBinding::inflate)

    override fun getIdNavHost() = R.id.nav_host_history

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(binding.toolbar)
        setupActionBarWithNavController(navHostFragment.findNavController())
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return super.onSupportNavigateUp() || navHostFragment.findNavController().navigateUp()
    }

    companion object {
        val REQUEST_CODE by lazy { Random.nextInt(100, 1000) }
    }
}