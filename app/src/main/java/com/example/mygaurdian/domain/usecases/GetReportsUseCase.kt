package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.model.CommunityReport
import com.example.mygaurdian.domain.repository.CommunityRepository

class GetReportsUseCase(private val repo: CommunityRepository) {
    suspend operator fun invoke(
        lat: Double, lng: Double, radius: Double
    ): List<CommunityReport> = repo.getReportsNearby(lat, lng, radius)
}
