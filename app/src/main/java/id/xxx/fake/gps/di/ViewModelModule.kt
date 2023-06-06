package id.xxx.fake.gps.di

import id.xxx.fake.gps.presentation.ui.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object ViewModelModule {
    val module = module {
        viewModel { HomeViewModel() }
    }
}