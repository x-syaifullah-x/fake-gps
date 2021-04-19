package id.xxx.fake.gps.history.presentation.di

import id.xxx.fake.gps.history.presentation.HistoryViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

object HistoryPresentationModule {
    private val viewModel = module {
        viewModel { HistoryViewModel(get()) }
    }

    val modules = listOf(viewModel)
}