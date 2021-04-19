package id.xxx.fake.gps.history.data.source.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity.Companion.FLH_DATE
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity.Companion.FLH_HISTORY_ID
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity.Companion.FLH_PRIMARY_KEY
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity.Companion.FLH_TABLE
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity.Companion.FLH_USER_ID

@Dao
interface HistoryDao {

    @Query("UPDATE $FLH_TABLE set $FLH_HISTORY_ID=:historyId where $FLH_PRIMARY_KEY=:id")
    suspend fun setHistoryId(id: Long, historyId: String): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: HistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: List<HistoryEntity>): List<Long>

    @Query("DELETE FROM $FLH_TABLE WHERE $FLH_PRIMARY_KEY=:id")
    suspend fun delete(id: Long): Int /* success delete always return 1 */

    @Query("DELETE FROM $FLH_TABLE WHERE $FLH_USER_ID=:userId")
    suspend fun deletes(userId: String?): Int

    @Query("delete from sqlite_sequence where name='$FLH_TABLE'")
    suspend fun resetPrimaryKey()

    @Query("SELECT $FLH_TABLE.* FROM $FLH_TABLE WHERE $FLH_USER_ID=:userId ORDER BY $FLH_PRIMARY_KEY DESC")
    fun getWithPagingSource(userId: String?): PagingSource<Int, HistoryEntity>

    @Query("SELECT DISTINCT $FLH_DATE FROM $FLH_TABLE WHERE $FLH_USER_ID=:userId ORDER BY $FLH_PRIMARY_KEY DESC")
    suspend fun getLastDate(userId: String?): Long?
}