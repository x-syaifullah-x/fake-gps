package id.xxx.fake.gps.di

import androidx.paging.ExperimentalPagingApi
import id.xxx.auth.di.AuthComponent
import id.xxx.fake.gps.database.AppDatabase
import id.xxx.fake.gps.history.di.HistoryComponent
import id.xxx.map.box.di.MapBoxComponent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.core.module.Module

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@FlowPreview
object AppModule {

    val modules: List<Module> = mutableListOf(
        AppDatabase.module, ViewModelModule.modules
    ).apply {
        addAll(HistoryComponent.getComponent(false))
        addAll(AuthComponent.getComponent(false))
        addAll(MapBoxComponent.getComponent(false))
    }
}