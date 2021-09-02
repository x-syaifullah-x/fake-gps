package id.xxx.fake.gps.presentation.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import id.xxx.fake.gps.presentation.provider.DataContract.AUTHORITY

class DataProvider : ContentProvider() {

    companion object {
        private val URI_MATCHER = UriMatcher(UriMatcher.NO_MATCH)
        private const val NAME_TABLE = 1
        private const val ID = 2

        init {
            URI_MATCHER.addURI(AUTHORITY, DataContract.DataColumns.PATH, NAME_TABLE)
            URI_MATCHER.addURI(AUTHORITY, "$${DataContract.DataColumns.PATH}/#", ID)
        }
    }

    override fun onCreate() = true

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (URI_MATCHER.match(uri)) {
            NAME_TABLE -> null
            ID -> null
            else -> null
        }
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val result: Long = when (NAME_TABLE) {
            URI_MATCHER.match(uri) -> 0
            else -> 0
        }
        return Uri.parse("${DataContract.DataColumns.CONTENT_URI}/$result")
    }

    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val result = when (ID) {
            URI_MATCHER.match(uri) -> {
                val id = "${uri.lastPathSegment}"; 1
            }
            else -> 0
        }
        context?.contentResolver?.notifyChange(DataContract.DataColumns.CONTENT_URI, null)
        return result
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        val result = when (ID) {
            URI_MATCHER.match(uri) -> {
                val id = "${uri.lastPathSegment}"; 1
            }
            else -> 0
        }
        context?.contentResolver?.notifyChange(DataContract.DataColumns.CONTENT_URI, null)
        return result
    }

    override fun getType(uri: Uri): String? = null
}
