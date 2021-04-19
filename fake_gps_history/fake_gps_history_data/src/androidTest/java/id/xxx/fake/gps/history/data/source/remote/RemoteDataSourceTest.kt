package id.xxx.fake.gps.history.data.source.remote

import com.google.firebase.firestore.FirebaseFirestore
import id.xxx.fake.gps.history.data.source.remote.response.HistoryResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.runBlocking
import org.junit.Test

@ExperimentalCoroutinesApi
class RemoteDataSourceTest {

    private val fireStore = FirebaseFirestore.getInstance()

    private val remoteDataSource = RemoteDataSource(fireStore)


    @Test
    fun get() = runBlocking<Unit> {
        remoteDataSource.getPage("user_id_1", null)
            .collect {
                println(it)
            }
    }

    @Test
    fun streamDataTest() = runBlocking<Unit> {
        val data = remoteDataSource.streamData("user_id_1")
        data.collect {
            println(it)
        }
    }

    @Test
    fun insertTest() = runBlocking {
        repeat(200) {
            remoteDataSource.insert(
                HistoryResponse(
                    userId = "user_id_1",
                    address = "address_${it}",
                    longitude = 1.1,
                    latitude = 1.1
                )
            )
            Thread.sleep(500)
        }
    }

    @Test
    fun updateTest() = runBlocking {
        remoteDataSource.update(
            HistoryResponse(
                userId = "user_id_1",
                id = "cLb0SF1AmuWt40fOnG2w",
                address = "address_update",
                longitude = 1.31313,
                latitude = 1.31131
            )
        )
        Thread.sleep(2000)
    }

    @Test
    fun deleteTest() = runBlocking {
        remoteDataSource.delete(userId = "user_id_1", "wWua84AcFRgqVh9olVx2")
        Thread.sleep(2000)
    }
}