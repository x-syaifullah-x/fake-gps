package id.xxx.fake.gps.presentation.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import id.xxx.auth.domain.user.usecase.IInteractor

class HomeViewModel(iInteractor: IInteractor) : ViewModel() {

    val currentUser = iInteractor.currentUser().asLiveData(viewModelScope.coroutineContext)

    val signOut = { iInteractor.signOut().asLiveData() }
}