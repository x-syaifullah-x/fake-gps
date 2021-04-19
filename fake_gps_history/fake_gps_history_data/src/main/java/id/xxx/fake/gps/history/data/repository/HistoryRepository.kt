package id.xxx.fake.gps.history.data.repository

import androidx.paging.*
import id.xxx.fake.gps.history.data.mapper.DataMapper.toHistoryEntity
import id.xxx.fake.gps.history.data.mapper.DataMapper.toHistoryModel
import id.xxx.fake.gps.history.data.mapper.DataMapper.toHistoryResponse
import id.xxx.fake.gps.history.data.source.local.LocalDataSource
import id.xxx.fake.gps.history.data.source.remote.RemoteDataSource
import id.xxx.fake.gps.history.domain.model.HistoryModel
import id.xxx.fake.gps.history.domain.repository.IRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class HistoryRepository(
    private val localDataSource: LocalDataSource,
    @Suppress("EXPERIMENTAL_API_USAGE")
    private val remoteDataSource: RemoteDataSource
) : IRepository<HistoryModel> {

    override suspend fun insert(model: HistoryModel) {
        val id = localDataSource.insert(model.toHistoryEntity())
        if (id != 0L) model.userId?.apply {
            val historyId = remoteDataSource.insert(model.toHistoryResponse())
            localDataSource.setHistoryId(id, historyId)
        }
    }

    override suspend fun delete(model: HistoryModel) {
        model.id?.apply {
            if (localDataSource.delete(this)) {
                if (model.userId != null && model.historyId != null)
                    remoteDataSource.delete(model.userId!!, model.historyId!!)
            }
        }
    }

    @ExperimentalPagingApi
    override fun getHistory(userId: String?): Flow<PagingData<HistoryModel>> {
        val mediator = userId?.run {
            HistoryRemoteMediator(
                blockGetPage = { localDataSource.getLastDate(userId) },
                blockRequest = { page -> remoteDataSource.getPage(userId, page) },
                blockOnRequest = {
                    it.forEach { hisRes ->
                        localDataSource.insert(hisRes.toHistoryEntity())
                    }
                }
            )
        }
        return Pager(
            config = PagingConfig(pageSize = 1, enablePlaceholders = true),
            remoteMediator = mediator,
            pagingSourceFactory = { localDataSource.getPaging(userId) }
        ).flow.flowOn(Dispatchers.IO).map { it.map { entity -> entity.toHistoryModel() } }
    }
}