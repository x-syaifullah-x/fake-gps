package id.xxx.fake.gps.history.data.mapper

import id.xxx.fake.gps.history.data.source.local.entity.HistoryEntity
import id.xxx.fake.gps.history.data.source.remote.response.HistoryResponse
import id.xxx.fake.gps.history.domain.model.HistoryModel

object DataMapper {

    fun HistoryModel.toHistoryEntity() = HistoryEntity(
        userId = userId,
        historyId = historyId,
        address = address,
        latitude = latitude,
        longitude = longitude,
        date = date
    )

    fun HistoryEntity.toHistoryModel() = HistoryModel(
        id = id,
        userId = userId,
        historyId = historyId,
        address = address,
        latitude = latitude,
        longitude = longitude,
        date = date
    )

    fun HistoryResponse.toHistoryEntity() = HistoryEntity(
        historyId = id,
        userId = userId,
        address = address,
        latitude = latitude,
        longitude = longitude,
        date = date
    )

    fun HistoryModel.toHistoryResponse() = HistoryResponse(
        userId = userId,
        address = address,
        latitude = latitude,
        longitude = longitude
    )
}