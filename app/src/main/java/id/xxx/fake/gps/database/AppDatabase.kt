package id.xxx.fake.gps.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.xxx.auth.data.email.source.local.dao.UserDao
import id.xxx.auth.data.email.source.local.entity.UserEntity
import id.xxx.fake.gps.history.data.source.local.dao.HistoryDao
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity
import id.xxx.map.box.search.data.source.local.dao.PlacesDao
import id.xxx.map.box.search.data.source.local.entity.PlacesEntity
import org.koin.dsl.module

@Database(
    entities = [
        HistoryEntity::class,
        UserEntity::class,
        PlacesEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): id.xxx.fake.gps.database.dao.UserDao

    abstract fun historyDao(): id.xxx.fake.gps.database.dao.HistoryDao

    abstract fun placesDao(): id.xxx.fake.gps.database.dao.PlacesDao

    companion object {

        private const val DATA_BASE_NAME = "id.xxx.fake.gps"

        val module = module {
            single { getInstance(get()) }
            single<HistoryDao> { get<AppDatabase>().historyDao() }
            single<UserDao> { get<AppDatabase>().userDao() }
            single<PlacesDao> { get<AppDatabase>().placesDao() }
        }

        @Volatile
        private var instance: AppDatabase? = null

        private fun getInstance(app: Application): AppDatabase {
            instance ?: synchronized(AppDatabase::class.java) {
                instance = Room.databaseBuilder(app, AppDatabase::class.java, DATA_BASE_NAME)
//                instance = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance as AppDatabase
        }
    }
}