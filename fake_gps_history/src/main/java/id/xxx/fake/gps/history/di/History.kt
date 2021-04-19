package id.xxx.fake.gps.history.di

import id.xxx.fake.gps.history.data.di.HistoryDataModule
import id.xxx.fake.gps.history.domain.di.HistoryDomainModule
import id.xxx.fake.gps.history.presentation.di.HistoryPresentationModule

object History {

    val modules = listOf(
        HistoryDataModule.module,
        HistoryDomainModule.module,
        HistoryPresentationModule.module
    )
}