package id.xxx.fake.gps.history.data.di

import androidx.paging.ExperimentalPagingApi
import com.google.firebase.firestore.FirebaseFirestore
import id.xxx.fake.gps.history.data.repository.HistoryRepository
import id.xxx.fake.gps.history.data.source.local.LocalDataSource
import id.xxx.fake.gps.history.data.source.remote.RemoteDataSource
import id.xxx.fake.gps.history.domain.model.HistoryModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import org.koin.dsl.module
import id.xxx.fake.gps.history.domain.repository.IRepository as IHistoryRepository

@ExperimentalPagingApi
@ExperimentalCoroutinesApi
@FlowPreview
object HistoryDataModule {

    private val firebaseModule = module {
        single { FirebaseFirestore.getInstance() }
    }

    private val dataSourceModule = module {
        single { LocalDataSource(get()) }
        single { RemoteDataSource(get()) }
    }

    private val repositoryModule = module {
        single<IHistoryRepository<HistoryModel>> { HistoryRepository(get(),get()) }
    }

    val modules = listOf(
        repositoryModule, dataSourceModule, firebaseModule
    )
}