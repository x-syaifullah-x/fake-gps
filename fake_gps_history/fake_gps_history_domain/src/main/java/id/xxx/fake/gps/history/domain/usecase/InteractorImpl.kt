package id.xxx.fake.gps.history.domain.usecase

import id.xxx.fake.gps.history.domain.model.HistoryModel
import id.xxx.fake.gps.history.domain.repository.IRepository

class InteractorImpl constructor(
    private val iRepository: IRepository<HistoryModel>
) : IInteractor {

    override fun getHistory(userId: String?) = iRepository.getHistory(userId)
    override suspend fun insert(model: HistoryModel) = iRepository.insert(model)
    override suspend fun delete(model: HistoryModel) = iRepository.delete(model)
}