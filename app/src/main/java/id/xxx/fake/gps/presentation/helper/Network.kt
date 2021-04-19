package id.xxx.fake.gps.presentation.helper

import android.content.Context
import androidx.work.*

object Network {

    fun <T : ListenableWorker> onConnected(context: Context, workerClass: Class<T>, data: Data) {
        val myConstraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val oneTimeWorkRequest = OneTimeWorkRequest
            .Builder(workerClass)
            .setInputData(data)
            .setConstraints(myConstraints)
            .build()
        WorkManager.getInstance(context).enqueue(oneTimeWorkRequest)
    }
}