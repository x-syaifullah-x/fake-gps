package id.xxx.fake.gps.user_handle.data.source.local

import androidx.room.withTransaction

class UserDataSourceLocal(private val dao: UserDao) {

    companion object {

        @Volatile
        private var INSTANCE: UserDataSourceLocal? = null

        fun getInstance(dao: UserDao) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: UserDataSourceLocal(dao)
                    .also { INSTANCE = it }
            }
    }

    fun getCurrentUser() = dao.getCurrentUser()

    suspend fun signIn(entity: UserEntity) {
        dao.room.withTransaction {
            dao.nukeTable()
            dao.insert(entity)
        }
    }

    suspend fun signOut() {
        dao.room.withTransaction {
            dao.nukeTable()
        }
    }
}