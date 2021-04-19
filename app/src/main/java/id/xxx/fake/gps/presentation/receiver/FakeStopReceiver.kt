package id.xxx.fake.gps.presentation.receiver

import android.content.ContextWrapper
import android.content.Intent
import id.xxx.fake.gps.presentation.service.FakeLocationService
import id.xxx.module.presentation.base.reciver.BaseReceiver

class FakeStopReceiver : BaseReceiver() {
    override fun onReceive(context: ContextWrapper, intent: Intent?) {
        if (intent?.action == "stop_fake_gps") {
            context.stopService(Intent(context, FakeLocationService::class.java))
        }
    }
}
