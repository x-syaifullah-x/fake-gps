package id.xxx.fake.gps.user_handle.data

import android.content.Context
import id.xxx.fake.gps.user_handle.data.local.UserDB
import id.xxx.fake.gps.user_handle.data.local.UserDataSourceLocal
import id.xxx.fake.gps.user_handle.data.local.UserEntity
import id.xxx.fake.gps.user_handle.domain.IUserHandle
import id.xxx.fake.gps.user_handle.domain.UserHandleModel
import id.xxx.module.model.sealed.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class UserHandleRepo private constructor(
    private val dataSourceLocal: UserDataSourceLocal
) : IUserHandle {

    companion object {

        @Volatile
        private var INSTANCE: IUserHandle? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val dao = UserDB.getInstance(context).userDao
                    val dataSourceLocal = UserDataSourceLocal.getInstance(dao)
                    UserHandleRepo(dataSourceLocal).also { INSTANCE = it }
                }
            }
    }

    override fun currentUser(): Flow<Resource<UserHandleModel>> {
        return dataSourceLocal.getCurrentUser().map { entity ->
            if (entity != null) {
                val model = UserHandleModel(entity.uid)
                Resource.Success(model)
            } else {
                Resource.Empty
            }
        }.catch {
            emit(Resource.Error(data = null, it))
        }
    }

    override fun signIn(model: UserHandleModel) {
        CoroutineScope(Dispatchers.IO).launch {
            dataSourceLocal.signIn(
                UserEntity(uid = model.uid)
            )
        }
    }

    override fun signOut() {
        CoroutineScope(Dispatchers.IO).launch {
            dataSourceLocal.signOut()
        }
    }
}