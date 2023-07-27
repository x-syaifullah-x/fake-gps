package id.xxx.fake.gps.user_handle.domain.usecase

import id.xxx.fake.gps.user_handle.domain.UserHandleModel
import id.xxx.module.model.sealed.Resource
import kotlinx.coroutines.flow.Flow

interface IUserHandleUseCase {

    fun currentUser(): Flow<Resource<UserHandleModel>>

    suspend fun signIn(model: UserHandleModel)

    suspend fun signOut()
}