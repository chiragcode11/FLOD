package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.model.CommunityReport
import com.example.mygaurdian.domain.repository.CommunityRepository

class AddReportUseCase(private val repo: CommunityRepository) {
    suspend operator fun invoke(report: CommunityReport): String =
        repo.addReport(report)
}
