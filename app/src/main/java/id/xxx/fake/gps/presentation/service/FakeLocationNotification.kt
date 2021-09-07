package id.xxx.fake.gps.presentation.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.android.gms.maps.model.LatLng
import id.xxx.fake.gps.R
import id.xxx.fake.gps.presentation.receiver.FakeStopReceiver
import id.xxx.fake.gps.presentation.ui.home.HomeActivity
import id.xxx.fake.gps.presentation.utils.formatDouble

class FakeLocationNotification(context: Context) {
    private val notificationManager = NotificationManagerCompat.from(context)
    private val intent = Intent(context, FakeStopReceiver::class.java).apply {
        action = "stop_fake_gps"
    }
    private val stopPendingIntent = PendingIntent.getBroadcast(
        context, 0, intent, 0
    )
    private val notificationBuilder = NotificationCompat.Builder(context, "100")
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("Mock Running")
        .setGroup(this::class.java.simpleName)
        .setShowWhen(false)
        .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
        .addAction(R.drawable.ic_action_call, "Stop", stopPendingIntent)
        .setContentIntent(
            PendingIntent.getActivity(
                context, 0, Intent(context, HomeActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK), 0
            )
        )

    init {
        val ncg = notificationManager.getNotificationChannelGroup(GROUP_ID_MOCK_STARTED)
        if (!notificationManager.notificationChannelGroups.contains(ncg)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                notificationManager.createNotificationChannelGroup(
                    NotificationChannelGroup(GROUP_ID_MOCK_STARTED, GROUP_NAME_MOCK_STARTED)
                )
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setChannelId("mock notification")
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    "mock notification", "mock notification", NotificationManager.IMPORTANCE_HIGH
                )
            )
        }
    }

    fun getNotification(): Notification {
        val notification = notificationBuilder.build()
        notification.flags = Notification.FLAG_NO_CLEAR
        return notification
    }

    fun show() = notificationManager.notify(ID_MOCK_NOTIFICATION, getNotification())

    fun cancel() = notificationManager.cancel(ID_MOCK_NOTIFICATION)

    fun setContentText(latLng: LatLng) {
        notificationBuilder.setContentText(
            "position : ${formatDouble(latLng.latitude)}, ${formatDouble(latLng.longitude)}"
        )
    }

    companion object {
        private const val GROUP_ID_MOCK_STARTED = "ID_MOCK_STARTED_01"
        private const val GROUP_NAME_MOCK_STARTED = "Mock Started"
        const val ID_MOCK_NOTIFICATION = 505
    }
}