package id.xxx.fake.gps.user_handle.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class UserDB : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE: UserDB? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(UserDB::class.java) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext, UserDB::class.java, "user_db"
                )
//                instance = Room.inMemoryDatabaseBuilder(app, UserDB::class.java)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build().also { INSTANCE = it }
            }
    }

    abstract val userDao: UserDao
}