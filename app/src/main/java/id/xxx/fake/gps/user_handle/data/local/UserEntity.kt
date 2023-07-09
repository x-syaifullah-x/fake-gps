package id.xxx.fake.gps.user_handle.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = UserEntity.TABLE_NAME
)
data class UserEntity(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = COLUMN_NAME_UID)
    val uid: String
) {
    companion object {

        const val TABLE_NAME = "user"

        const val COLUMN_NAME_UID = "uid"
    }
}