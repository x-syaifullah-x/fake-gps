package id.xxx.fake.gps.user_handle.data.repository

import android.content.Context
import id.xxx.fake.gps.user_handle.data.source.local.UserDB
import id.xxx.fake.gps.user_handle.data.source.local.UserDataSourceLocal
import id.xxx.fake.gps.user_handle.data.source.local.UserEntity
import id.xxx.fake.gps.user_handle.domain.repository.IUserHandleRepository
import id.xxx.fake.gps.user_handle.domain.UserHandleModel
import id.xxx.module.model.sealed.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class UserHandleRepoRepository private constructor(
    private val dataSourceLocal: UserDataSourceLocal
) : IUserHandleRepository {

    companion object {

        @Volatile
        private var INSTANCE: IUserHandleRepository? = null

        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    val dao = UserDB.getInstance(context).userDao
                    val dataSourceLocal = UserDataSourceLocal.getInstance(dao)
                    UserHandleRepoRepository(dataSourceLocal).also { INSTANCE = it }
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

    override suspend fun signIn(model: UserHandleModel) {
        withContext(Dispatchers.IO) {
            dataSourceLocal.signIn(
                UserEntity(uid = model.uid)
            )
        }
    }

    override suspend fun signOut() {
        withContext(Dispatchers.IO) {
            dataSourceLocal.signOut()
        }
    }
}