package id.xxx.fake.gps.user_handle.domain

import id.xxx.module.model.sealed.Resource
import kotlinx.coroutines.flow.Flow

interface IUserHandle {

    fun currentUser(): Flow<Resource<UserHandleModel>>

    fun signIn(model: UserHandleModel)

    fun signOut()
}