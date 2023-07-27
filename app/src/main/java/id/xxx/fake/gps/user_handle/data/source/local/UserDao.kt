package id.xxx.fake.gps.user_handle.data.source.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class UserDao(val room: RoomDatabase) {

    @Query("DELETE FROM ${UserEntity.TABLE_NAME}")
    abstract fun nukeTable()

    @Query("SELECT * FROM ${UserEntity.TABLE_NAME}")
    abstract fun getCurrentUser(): Flow<UserEntity?>

    @Insert
    abstract fun insert(entity: UserEntity): Long

    @Update
    abstract fun update(entity: UserEntity): Int

    @Query("SELECT EXISTS(SELECT * FROM ${UserEntity.TABLE_NAME} WHERE ${UserEntity.COLUMN_NAME_UID} = :uid)")
    abstract fun isRowIsExist(uid: String): Boolean

    @Query("DELETE FROM ${UserEntity.TABLE_NAME} WHERE ${UserEntity.COLUMN_NAME_UID} = :uid")
    abstract fun deleteByUID(uid: String)
}