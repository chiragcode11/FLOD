package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.repository.TeamRepository

class AddTeamUseCase(private val repo: TeamRepository) {
    suspend operator fun invoke(name: String, memberIds: List<String>) =
        repo.addTeam(name, memberIds)
}
