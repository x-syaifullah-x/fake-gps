package id.xxx.fake.gps.history.data.source.remote.response

import com.google.firebase.firestore.QueryDocumentSnapshot

data class HistoryResponse(

    val userId: String?,

    val id: String = "",

    val address: String,

    val latitude: Double,

    val longitude: Double,

    val date: Long = System.currentTimeMillis()

) {
    companion object {
        const val HISTORY_ID = "id"
        const val USER_UID = "user_uid"
        const val ADDRESS = "address"
        const val LATITUDE = "latitude"
        const val LONGITUDE = "longitude"
        const val DATE = "date"
    }
}

fun QueryDocumentSnapshot.toHistoryFireStoreResponse() = HistoryResponse(
    id = id,
    userId = getString(HistoryResponse.USER_UID) ?: "-",
    address = getString(HistoryResponse.ADDRESS) ?: "-",
    latitude = getDouble(HistoryResponse.LATITUDE) ?: 0.0,
    longitude = getDouble(HistoryResponse.LONGITUDE) ?: 0.0,
    date = getLong(HistoryResponse.DATE) ?: 0
)