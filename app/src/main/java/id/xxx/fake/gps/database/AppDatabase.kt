package id.xxx.fake.gps.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.xxx.fake.gps.history.data.source.local.dao.HistoryDao
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity
import id.xxx.map.box.search.data.source.local.dao.PlacesDao
import id.xxx.map.box.search.data.source.local.entity.PlacesEntity
import org.koin.dsl.module

@Database(
    entities = [
        HistoryEntity::class,
        PlacesEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    abstract fun placesDao(): PlacesDao

    companion object {

        val module = module {
            single { getInstance(get()) }
            single { get<AppDatabase>().historyDao() }
            single { get<AppDatabase>().placesDao() }
        }

        @Volatile
        private var instance: AppDatabase? = null

        private fun getInstance(app: Application) =
            instance ?: synchronized(AppDatabase::class.java) {
                instance ?: Room.databaseBuilder(app, AppDatabase::class.java, "id.xxx.fake.gps")
//                instance = Room.inMemoryDatabaseBuilder(app, AppDatabase::class.java)
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build().also { instance = it }
            }
    }
}