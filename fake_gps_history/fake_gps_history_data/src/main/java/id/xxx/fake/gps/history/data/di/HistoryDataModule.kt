package id.xxx.fake.gps.history.data.di

import com.google.firebase.firestore.FirebaseFirestore
import id.xxx.fake.gps.history.data.repository.HistoryRepository
import id.xxx.fake.gps.history.data.source.local.LocalDataSource
import id.xxx.fake.gps.history.data.source.remote.RemoteDataSource
import id.xxx.fake.gps.history.domain.model.HistoryModel
import org.koin.dsl.module
import id.xxx.fake.gps.history.domain.repository.IRepository as IHistoryRepository

object HistoryDataModule {

    val module = module {
        single { FirebaseFirestore.getInstance() }
        single { LocalDataSource(get()) }
        single { RemoteDataSource(get()) }
        single<IHistoryRepository<HistoryModel>> { HistoryRepository(get(), get()) }
    }
}