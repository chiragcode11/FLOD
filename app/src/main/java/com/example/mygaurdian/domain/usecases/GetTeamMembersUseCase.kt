package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.model.User
import com.example.mygaurdian.domain.repository.TeamRepository

class GetTeamMembersUseCase(private val repo: TeamRepository) {
    suspend operator fun invoke(teamId: String): List<User> =
        repo.getTeamMembers(teamId)
}
