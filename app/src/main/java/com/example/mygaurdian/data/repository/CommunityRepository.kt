package com.example.mygaurdian.domain.repository

import com.example.mygaurdian.domain.model.CommunityReport

interface CommunityRepository {
    suspend fun getReportsNearby(
        centerLat: Double, centerLng: Double, radiusMeters: Double
    ): List<CommunityReport>

    suspend fun addReport(report: CommunityReport): String

    suspend fun upvoteReport(reportId: String, userId: String)
}
