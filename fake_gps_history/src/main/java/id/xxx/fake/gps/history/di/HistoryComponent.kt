package id.xxx.fake.gps.history.di

import androidx.paging.ExperimentalPagingApi
import id.xxx.fake.gps.history.data.di.HistoryDataModule
import id.xxx.fake.gps.history.database.HistoryDatabase
import id.xxx.fake.gps.history.domain.di.HistoryDomainModule
import id.xxx.fake.gps.history.presentation.di.HistoryPresentationModule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.module.Module

@FlowPreview
@ExperimentalPagingApi
@ExperimentalCoroutinesApi
object HistoryComponent {

    private val historyComponent = mutableListOf<Module>().apply {
        addAll(HistoryDataModule.modules)
        addAll(HistoryDomainModule.modules)
        addAll(HistoryPresentationModule.modules)
    }

    fun getComponent(includeDatabase: Boolean = true): List<Module> {
        return if (includeDatabase) {
            historyComponent.apply {
                add(HistoryDatabase.module)
            }
        } else {
            historyComponent
        }
    }
}