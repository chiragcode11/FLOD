package com.example.mygaurdian.data.repository

import com.example.mygaurdian.data.remote.TeamRemoteDataSource
import com.example.mygaurdian.domain.model.User
import com.example.mygaurdian.domain.repository.TeamRepository

class TeamRepositoryImpl(
    private val ds: TeamRemoteDataSource
) : TeamRepository {
    override suspend fun getTeams() = ds.getTeams()
    override suspend fun toggleSharing(teamId: String, sharing: Boolean) =
        ds.toggleSharing(teamId, sharing)
    override suspend fun addTeam(name: String, memberIds: List<String>) =
        ds.addTeam(name, memberIds)
    override suspend fun deleteTeam(teamId: String) = ds.deleteTeam(teamId)
    override suspend fun getTeamMembers(teamId: String): List<User> =
        ds.getTeamMembers(teamId)
    override suspend fun inviteMember(teamId: String, memberId: String, teamName: String) =
        ds.inviteMember(teamId, memberId, teamName)
}
