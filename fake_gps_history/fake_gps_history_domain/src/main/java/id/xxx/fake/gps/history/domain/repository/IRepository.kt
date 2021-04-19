package id.xxx.fake.gps.history.domain.repository

import androidx.paging.PagingData
import id.xxx.base.domain.model.BaseModel
import kotlinx.coroutines.flow.Flow

interface IRepository<Model : BaseModel<*>> {
    fun getHistory(userId: String?): Flow<PagingData<Model>>
    suspend fun insert(model: Model)
    suspend fun delete(model: Model)
}