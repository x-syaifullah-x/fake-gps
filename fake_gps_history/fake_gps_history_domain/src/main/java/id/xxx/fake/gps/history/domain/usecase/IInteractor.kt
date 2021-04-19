package id.xxx.fake.gps.history.domain.usecase

import androidx.paging.PagingData
import id.xxx.fake.gps.history.domain.model.HistoryModel
import kotlinx.coroutines.flow.Flow

interface IInteractor {

    fun getHistory(userId: String?): Flow<PagingData<HistoryModel>>
    suspend fun insert(model: HistoryModel)
    suspend fun delete(model: HistoryModel)
}