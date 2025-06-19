package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.repository.SOSRepository

class SendSOSUseCase(private val repo: SOSRepository) {
    suspend operator fun invoke(teamId: String, lat: Double, lng: Double) =
        repo.sendSOS(teamId, lat, lng)
}
