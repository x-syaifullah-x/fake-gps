package id.xxx.fake.gps.presentation.provider

import android.net.Uri
import android.provider.BaseColumns

object DataContract {
    const val AUTHORITY = "id.xxx.fake.gps.provider"
    const val SCHEME = "content"

    class DataColumns : BaseColumns {
        companion object {
            const val PATH = "data"

            // created URI content://{AUTHORITY}/data
            val CONTENT_URI: Uri = Uri.Builder().scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(PATH)
                .build()
        }

    }
}