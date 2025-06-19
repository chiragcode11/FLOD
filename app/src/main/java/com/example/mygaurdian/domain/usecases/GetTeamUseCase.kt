package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.model.Team
import com.example.mygaurdian.domain.repository.TeamRepository

class GetTeamsUseCase(
    private val repo: TeamRepository
) {
    suspend operator fun invoke(): List<Team> =
        repo.getTeams()
}
