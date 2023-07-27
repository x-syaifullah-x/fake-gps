package id.xxx.fake.gps.user_handle.domain.usecase

import id.xxx.fake.gps.user_handle.domain.UserHandleModel
import id.xxx.fake.gps.user_handle.domain.repository.IUserHandleRepository

class UserHandleImpl private constructor(
    private val repository: IUserHandleRepository
) : IUserHandleUseCase {

    companion object {

        @Volatile
        private var INSTANCE: IUserHandleUseCase? = null

        fun getInstance(repository: IUserHandleRepository) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: run {
                    UserHandleImpl(repository).also { INSTANCE = it }
                }
            }
    }

    override fun currentUser() =
        repository.currentUser()

    override suspend fun signIn(model: UserHandleModel) =
        repository.signIn(model)

    override suspend fun signOut() =
        repository.signOut()
}