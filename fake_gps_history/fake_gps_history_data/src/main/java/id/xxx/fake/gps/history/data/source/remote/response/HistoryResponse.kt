package id.xxx.fake.gps.history.data.source.remote.response

import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.gson.annotations.SerializedName

data class HistoryResponse(

    @SerializedName(USER_ID) val userId: String?,

    @SerializedName(HISTORY_ID) val id: String = "",

    @SerializedName(ADDRESS) val address: String,

    @SerializedName(LATITUDE) val latitude: Double,

    @SerializedName(LONGITUDE) val longitude: Double,

    @SerializedName(DATE) val date: Long = System.currentTimeMillis()

) {
    companion object {
        const val HISTORY_ID = "id"
        const val USER_ID = "userId"
        const val ADDRESS = "address"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val DATE = "date"
    }
}

fun QueryDocumentSnapshot.toHistoryFireStoreResponse() = HistoryResponse(
    id = id,
    userId = getString(HistoryResponse.USER_ID) ?: "-",
    address = getString(HistoryResponse.ADDRESS) ?: "-",
    latitude = getDouble(HistoryResponse.LATITUDE) ?: 0.0,
    longitude = getDouble(HistoryResponse.LONGITUDE) ?: 0.0,
    date = getLong(HistoryResponse.DATE) ?: 0
)