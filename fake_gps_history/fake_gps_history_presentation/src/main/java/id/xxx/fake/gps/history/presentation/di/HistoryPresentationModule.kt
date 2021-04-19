package id.xxx.fake.gps.history.presentation.di

import id.xxx.fake.gps.history.presentation.HistoryViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object HistoryPresentationModule {

    val module = module {
        viewModel { HistoryViewModel(get()) }
    }
}