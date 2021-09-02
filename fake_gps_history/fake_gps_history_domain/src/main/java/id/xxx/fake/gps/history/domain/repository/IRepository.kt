package id.xxx.fake.gps.history.domain.repository

import androidx.paging.PagingData
import id.xxx.module.domain.model.IModel
import kotlinx.coroutines.flow.Flow

interface IRepository<Model : IModel<*>> {
    fun getHistory(userId: String?): Flow<PagingData<Model>>
    suspend fun insert(model: Model)
    suspend fun delete(model: Model)
}