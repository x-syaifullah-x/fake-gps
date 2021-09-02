package id.xxx.fake.gps.history.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import id.xxx.fake.gps.history.data.source.remote.response.HistoryResponse
import id.xxx.fake.gps.history.data.source.remote.response.toHistoryFireStoreResponse
import id.xxx.module.data.helper.DataHelper
import id.xxx.module.data.network.model.Result
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class RemoteDataSource(private val fireStore: FirebaseFirestore) {

    private fun collectionReference(userId: String?) = fireStore
        .collection("fake-gps")
        .document(userId ?: throw Throwable())
        .collection("history")

    @Suppress("EXPERIMENTAL_API_USAGE")
    fun streamData(userId: String) = callbackFlow {
        val listenerRegistration = collectionReference(userId)
            .addSnapshotListener { value, _ ->
                val data = value?.documentChanges
                val apiResponse =
                    if (!data.isNullOrEmpty()) {
                        id.xxx.module.data.network.model.Result.Success(data.map { it.document.toHistoryFireStoreResponse() })
                    } else {
                        Result.Empty
                    }
                offer(apiResponse)
            }; awaitClose { listenerRegistration.remove() }
    }

    fun getPage(
        userId: String,
        startAfterByDate: Long? = null,
        limit: Long = 20
    ) = DataHelper.getAsFlow(
        blockFetch = {
            val col = collectionReference(userId)
                .orderBy(HistoryResponse.DATE, Query.Direction.DESCENDING)
            if (startAfterByDate != null) {
                col.startAfter(startAfterByDate).limit(limit)
            } else {
                col.limit(limit)
            }.get().await().map { it.toHistoryFireStoreResponse() }
        },
        blockOnFetch = { it.isNotEmpty() }
    )

    suspend fun refresh(userId: String, startByDate: Long, endByDate: Long) {
        collectionReference(userId)
            .orderBy(HistoryResponse.DATE)
            .startAt(startByDate)
            .endAt(endByDate)
            .get()
            .await()
            .map { it.toHistoryFireStoreResponse() }
    }

    suspend fun insert(data: HistoryResponse): String {
        val documentReference = collectionReference(data.userId).add(data).await()
        documentReference.update(HistoryResponse.HISTORY_ID, documentReference.id)
        return documentReference.id
    }

    suspend fun update(data: HistoryResponse) {
        val dataMap = mapOf(
            HistoryResponse.ADDRESS to data.address,
            HistoryResponse.LATITUDE to data.latitude,
            HistoryResponse.LONGITUDE to data.longitude
        )
        collectionReference(data.userId).document(data.id).update(dataMap).await()
    }

    suspend fun delete(userId: String, historyId: String) {
        collectionReference(userId).document(historyId).delete().await()
    }
}