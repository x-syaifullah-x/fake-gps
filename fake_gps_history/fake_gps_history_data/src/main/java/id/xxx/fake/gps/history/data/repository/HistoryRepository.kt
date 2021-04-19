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
    private val remoteDataSource: RemoteDataSource
) : IRepository<HistoryModel> {

    companion object {
        private const val LIMIT_I: Int = 20
        private const val LIMIT_L: Long = LIMIT_I.toLong()
    }

    override suspend fun insert(model: HistoryModel) {
        val id = localDataSource.insert(model.toHistoryEntity())
        if (id != 0L) model.userId?.apply {
            val historyId = remoteDataSource.insert(model.toHistoryResponse())
            localDataSource.setHistoryId(id, historyId)
        }
    }

    override suspend fun delete(model: HistoryModel) {
        val id = model.id ?: return
        if (localDataSource.delete(id)) {
            val userId = model.userId ?: return
            val hisId = model.historyId ?: return
            remoteDataSource.delete(userId, hisId)
        }
    }

    @OptIn(ExperimentalPagingApi::class)
    override fun getHistory(userId: String?): Flow<PagingData<HistoryModel>> {
        val mediator = userId?.run {
            HistoryRemoteMediator(
                blockGetPage = { localDataSource.getLastDate(userId) },
                blockRequest = { page ->
                    remoteDataSource.getPage(userId, page, LIMIT_L)
                },
                blockOnRequest = { page, it ->
                    if ((page == null || page == 1L) && it.isEmpty()) {
                        localDataSource.deletes(userId)
                    } else {
                        localDataSource.insert(it.map { hr -> hr.toHistoryEntity() })
                    }
                }
            )
        }
        return Pager(
            config = PagingConfig(pageSize = LIMIT_I, enablePlaceholders = false),
            remoteMediator = mediator,
            pagingSourceFactory = {
                localDataSource.getPaging(userId)
            }
        ).flow
            .map { it.map { entity -> entity.toHistoryModel() } }
            .flowOn(Dispatchers.IO)
    }
}