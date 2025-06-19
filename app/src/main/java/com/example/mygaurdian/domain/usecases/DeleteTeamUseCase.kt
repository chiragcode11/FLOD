package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.repository.TeamRepository

class DeleteTeamUseCase(private val repo: TeamRepository) {
    suspend operator fun invoke(teamId: String) = repo.deleteTeam(teamId)
}
