package id.xxx.fake.gps.history.data.source.local

import androidx.paging.PagingSource
import id.xxx.fake.gps.history.data.source.local.dao.HistoryDao
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity

class LocalDataSource(private val dao: HistoryDao) {

    private fun validationUserId(userId: String?): String =
        if (userId.isNullOrBlank()) HistoryEntity.NO_USER_ID else userId

    suspend fun setHistoryId(id: Long, historyId: String): Int =
        dao.setHistoryId(id, historyId)

    suspend fun insert(entity: HistoryEntity): Long =
        dao.insert(entity.copy(userId = validationUserId(entity.userId)))

    suspend fun insert(entity: List<HistoryEntity>): List<Long> =
        dao.insert(entity.map { it.copy(userId = validationUserId(it.userId)) })

    suspend fun delete(id: Long): Boolean =
        dao.delete(id) == 1

    suspend fun deletes(userId: String?): Int =
        dao.deletes(userId)

    suspend fun getLastDate(userId: String?): Long? =
        dao.getLastDate(userId)

    fun getPaging(userId: String?): PagingSource<Int, HistoryEntity> {
        return dao.getWithPagingSource(validationUserId(userId))
    }
}