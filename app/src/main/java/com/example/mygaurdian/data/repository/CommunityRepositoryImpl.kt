package com.example.mygaurdian.data.repository

import com.example.mygaurdian.data.remote.CommunityRemoteDataSource
import com.example.mygaurdian.domain.model.CommunityReport
import com.example.mygaurdian.domain.repository.CommunityRepository

class CommunityRepositoryImpl(
    private val remote: CommunityRemoteDataSource
) : CommunityRepository {
    override suspend fun getReportsNearby(
        centerLat: Double, centerLng: Double, radiusMeters: Double
    ): List<CommunityReport> =
        remote.getReportsNearby(centerLat, centerLng, radiusMeters)

    override suspend fun addReport(report: CommunityReport): String =
        remote.addReport(report)

    override suspend fun upvoteReport(reportId: String, userId: String) =
        remote.upvoteReport(reportId, userId)
}
