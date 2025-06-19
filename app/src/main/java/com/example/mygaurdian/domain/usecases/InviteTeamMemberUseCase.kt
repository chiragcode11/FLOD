package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.repository.TeamRepository

class InviteMemberUseCase(private val repo: TeamRepository) {
    suspend operator fun invoke(teamId: String, memberId: String, teamName: String) =
        repo.inviteMember(teamId, memberId, teamName)
}
