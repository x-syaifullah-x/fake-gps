package id.xxx.fake.gps.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import id.xxx.auth.domain.email.usecase.AuthEmailInteractor

class HomeViewModel(iInteractor: AuthEmailInteractor) : ViewModel() {

    val currentUser = iInteractor.currentUser().asLiveData(viewModelScope.coroutineContext)

    val signOut = { iInteractor.signOut().asLiveData() }
}