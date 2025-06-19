package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.repository.CommunityRepository

class UpvoteReportUseCase(private val repo: CommunityRepository) {
    suspend operator fun invoke(reportId: String, userId: String) =
        repo.upvoteReport(reportId, userId)
}
