package id.xxx.fake.gps.history.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import id.xxx.fake.gps.history.data.source.remote.response.HistoryResponse
import id.xxx.fake.gps.history.data.source.remote.response.toHistoryFireStoreResponse
import id.xxx.module.common.helper.DataHelper
import id.xxx.module.common.model.sealed.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class RemoteDataSource(private val firebaseFirestore: FirebaseFirestore) {

    private fun collectionReference(userId: String?) = firebaseFirestore
        .collection("fake-gps")
        .document(userId ?: throw Throwable("can't access unregistered users"))
        .collection("history")

    fun streamData(userId: String): Flow<Result<Result<List<HistoryResponse>>>> {
        var listenerRegistration: ListenerRegistration? = null
        return DataHelper.getAsFlow(
            blockOnOpen = { callback ->
                listenerRegistration = collectionReference(userId)
                    .addSnapshotListener { querySnapshot, exception ->
                        val data = querySnapshot?.documentChanges
                        val apiResponse =
                            if (!data.isNullOrEmpty()) {
                                Result.Success(data.map { it.document.toHistoryFireStoreResponse() })
                            } else {
                                Result.Empty
                            }
                        if (exception != null) {
                            callback.error(exception)
                        } else {
                            callback.success(apiResponse)
                        }
                    }
            },
            blockOnClose = { listenerRegistration?.remove() }
        )
    }

    fun getPage(userId: String, startAfterByDate: Long?, limit: Long) =
        DataHelper.getAsFlow(
            blockFetch = {
                val query = collectionReference(userId)
                    .orderBy(HistoryResponse.DATE, Query.Direction.DESCENDING)
                if (startAfterByDate != null) {
                    query.startAfter(startAfterByDate).limit(limit)
                } else {
                    query.limit(limit)
                }

                return@getAsFlow query
                    .get()
                    .await()
                    .map { it.toHistoryFireStoreResponse() }
            },
            blockOnFetch = { it.isNotEmpty() }
        )

    suspend fun insert(data: HistoryResponse): String {
        val map = mapOf(
            HistoryResponse.USER_UID to data.userId,
            HistoryResponse.ADDRESS to data.address,
            HistoryResponse.LATITUDE to data.latitude,
            HistoryResponse.LONGITUDE to data.longitude,
            HistoryResponse.DATE to data.date
        )
        val docRef = collectionReference(data.userId).add(map).await()
        docRef.update(HistoryResponse.HISTORY_ID, docRef.id)
        return docRef.id
    }

    suspend fun update(data: HistoryResponse) {
        val map = mapOf(
            HistoryResponse.ADDRESS to data.address,
            HistoryResponse.LATITUDE to data.latitude,
            HistoryResponse.LONGITUDE to data.longitude
        )
        collectionReference(data.userId).document(data.id).update(map).await()
    }

    suspend fun delete(userId: String, historyId: String) {
        collectionReference(userId).document(historyId).delete().await()
    }
}