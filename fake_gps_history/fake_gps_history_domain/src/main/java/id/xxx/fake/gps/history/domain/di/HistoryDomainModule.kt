package id.xxx.fake.gps.history.domain.di

import org.koin.dsl.module
import id.xxx.fake.gps.history.domain.usecase.IInteractor as IHistoryInteractor
import id.xxx.fake.gps.history.domain.usecase.InteractorImpl as HistoryInteractor

object HistoryDomainModule {
    private val useCaseHistoryModule = module {
        single<IHistoryInteractor> { HistoryInteractor(get()) }
    }

    val modules = listOf(
        useCaseHistoryModule,
    )
}