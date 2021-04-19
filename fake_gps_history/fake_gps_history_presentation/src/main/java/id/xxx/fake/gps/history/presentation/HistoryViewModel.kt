package id.xxx.fake.gps.history.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import id.xxx.fake.gps.history.domain.model.HistoryModel
import id.xxx.fake.gps.history.domain.usecase.IInteractor
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val iInteractor: IInteractor,
) : ViewModel() {

    val data = { userId: String? ->
        iInteractor.getHistory(userId).cachedIn(viewModelScope).asLiveData()
    }

    val delete = { data: HistoryModel -> viewModelScope.launch { iInteractor.delete(data) } }
}