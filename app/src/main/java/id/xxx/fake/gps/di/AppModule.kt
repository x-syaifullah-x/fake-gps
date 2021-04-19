package id.xxx.fake.gps.di

import id.xxx.auth.di.Auth
import id.xxx.fake.gps.database.AppDatabase
import id.xxx.fake.gps.history.di.History
import id.xxx.map.box.search.di.SearchModule
import org.koin.core.module.Module

object AppModule {

    val modules: List<Module> = mutableListOf(
        AppDatabase.module, ViewModelModule.module
    ).apply {
        addAll(Auth.modules)
        addAll(History.modules)
        addAll(SearchModule.modules)
    }
}