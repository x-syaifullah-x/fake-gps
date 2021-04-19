package id.xxx.fake.gps.history.database

import android.app.Application
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import id.xxx.fake.gps.history.data.source.local.dao.IHistoryDao
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity
import org.koin.dsl.module

@Database(
    entities = [
        HistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class HistoryDatabase : RoomDatabase(), IHistoryDao {

    companion object {

        val module = module {
            single { getInstance(get()) }
            single { get<HistoryDatabase>().historyDao() }
        }

        @Volatile
        private var instance: HistoryDatabase? = null

        private fun getInstance(app: Application): HistoryDatabase {
            instance ?: synchronized(HistoryDatabase::class.java) {
                instance = Room.databaseBuilder(app, HistoryDatabase::class.java, "id.xxx.fake.gps.history")
//                instance = Room.inMemoryDatabaseBuilder(app, HistoryDatabase::class.java)
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
//                    .openHelperFactory(SupportFactory(SQLiteDatabase.getBytes("xxx.base.data".toCharArray())))
                    .build()
            }
            return instance as HistoryDatabase
        }
    }
}