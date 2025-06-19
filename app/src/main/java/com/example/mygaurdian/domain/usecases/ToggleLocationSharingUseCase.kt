package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.repository.TeamRepository

class ToggleLocationSharingUseCase(private val repo: TeamRepository) {
    suspend operator fun invoke(teamId: String, sharing: Boolean) =
        repo.toggleSharing(teamId, sharing)
}
