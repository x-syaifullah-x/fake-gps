package id.xxx.fake.gps.presentation.service

import android.content.Intent
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.widget.Toast
import androidx.core.content.getSystemService
import androidx.work.Data
import com.google.android.gms.maps.model.LatLng
import id.xxx.fake.gps.mapper.toHistoryModel
import id.xxx.fake.gps.presentation.helper.Network
import id.xxx.fake.gps.presentation.ui.home.HomeActivity
import id.xxx.fake.gps.presentation.workers.MyWorker
import id.xxx.map.box.search.domain.usecase.IInteractor
import id.xxx.module.common.model.sealed.Resource
import id.xxx.module.presentation.base.service.BaseService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import id.xxx.fake.gps.history.domain.usecase.IInteractor as IHistoryInteractor

class FakeLocationService : BaseService(), FakeLocation.Callback {
    private val iHistoryRepo: IHistoryInteractor by inject()
    private val search by inject<IInteractor>()

    private lateinit var fakeLocation: FakeLocation
    private lateinit var fakeNotification: FakeLocationNotification

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun onCreate() {
        super.onCreate()
        fakeLocation = FakeLocation.getInstance(getSystemService(), this)
        fakeNotification = FakeLocationNotification(baseContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        intent?.apply {
            latitude = getDoubleExtra("latitude", 0.0)
            longitude = getDoubleExtra("longitude", 0.0)
            fakeLocation.run(latitude, longitude)

            CoroutineScope(Dispatchers.IO).launch {
                val address = search.getAddress("$latitude,$longitude").drop(1).first()
                if (address is Resource.Success) {
                    val data = address.data
                    iHistoryRepo.insert(data.toHistoryModel(getStringExtra(HomeActivity.USER_ID_DATA_EXTRA)))
                } else if (address is Resource.Error) {
                    Network.onConnected(
                        baseContext, MyWorker::class.java, Data.Builder()
                            .putString(MyWorker.DATA_EXTRA, "$latitude,$longitude")
                            .putString(
                                HomeActivity.USER_ID_DATA_EXTRA,
                                getStringExtra(HomeActivity.USER_ID_DATA_EXTRA)
                            )
                            .build()
                    )
                }
            }
        } ?: stopSelf()
        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
        fakeLocation.stop()
    }

    override fun status(status: FakeLocation.Status) {
        when (status) {
            FakeLocation.Status.RUNNING -> {
                fakeNotification.setContentText(LatLng(latitude, longitude))
                if (VERSION.SDK_INT >= VERSION_CODES.O) {
                    startForeground(1, fakeNotification.getNotification())
                } else {
                    fakeNotification.show()
                }
            }
            FakeLocation.Status.STOP -> fakeNotification.cancel()
            FakeLocation.Status.ERROR_SECURITY_EXCEPTION -> {
                Toast.makeText(baseContext, "please enable mock location", Toast.LENGTH_LONG).show()
                stopSelf()
            }
            FakeLocation.Status.ERROR_LAT_LONG -> stopSelf()
            FakeLocation.Status.GPS_OFF -> {
                stopSelf()
                Toast.makeText(this, "please enable gps", Toast.LENGTH_LONG).show()
            }
        }
    }
}