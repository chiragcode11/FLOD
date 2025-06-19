package com.example.mygaurdian.data.repository

import com.example.mygaurdian.data.remote.SOSRemoteDataSource
import com.example.mygaurdian.domain.model.SOSEvent
import com.example.mygaurdian.domain.repository.SOSRepository

class SOSRepositoryImpl(
    private val remote: SOSRemoteDataSource
) : SOSRepository {
    override suspend fun sendSOS(teamId: String, lat: Double, lng: Double) =
        remote.sendSOS(teamId, lat, lng)

    override suspend fun getSOSHistory(): List<SOSEvent> =
        remote.getSOSHistory()
}
