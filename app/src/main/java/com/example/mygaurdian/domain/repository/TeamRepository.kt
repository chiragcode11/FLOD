package com.example.mygaurdian.domain.repository

import com.example.mygaurdian.domain.model.Team
import com.example.mygaurdian.domain.model.User

interface TeamRepository {
    suspend fun getTeams(): List<Team>
    suspend fun toggleSharing(teamId: String, sharing: Boolean)
    suspend fun addTeam(name: String, memberIds: List<String>)
    suspend fun deleteTeam(teamId: String)
    suspend fun getTeamMembers(teamId: String): List<User>
    suspend fun inviteMember(teamId: String, memberId: String, teamName: String)
}
