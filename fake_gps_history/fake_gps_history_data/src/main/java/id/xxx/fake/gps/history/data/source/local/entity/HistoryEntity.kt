package id.xxx.fake.gps.history.data.source.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity.Companion.FLH_HISTORY_ID
import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity.Companion.FLH_TABLE
import id.xxx.module.common.model.IModel

@Entity(
    tableName = FLH_TABLE,
    indices = [Index(value = [FLH_HISTORY_ID], unique = true)]
)
data class HistoryEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = FLH_PRIMARY_KEY)
    override val id: Long? = null,

    @ColumnInfo(name = FLH_USER_ID)
    val userId: String? = NO_USER_ID,

    @ColumnInfo(name = FLH_HISTORY_ID)
    val historyId: String? = null,

    @ColumnInfo(name = FLH_ADDRESS)
    val address: String,

    @ColumnInfo(name = FLH_LATITUDE)
    val latitude: Double,

    @ColumnInfo(name = FLH_LONGITUDE)
    val longitude: Double,

    @ColumnInfo(name = FLH_DATE)
    val date: Long

) : IModel<Long> {
    companion object {
        const val FLH_TABLE = "fake_location_history"
        const val NO_USER_ID = "no-user-id"
        const val FLH_HISTORY_ID = "history_id"
        const val FLH_USER_ID = "user_id"
        const val FLH_PRIMARY_KEY = "primary_key"
        const val FLH_ADDRESS = "address"
        const val FLH_LATITUDE = "latitude"
        const val FLH_LONGITUDE = "longitude"
        const val FLH_DATE = "date"
    }
}