package com.example.mygaurdian.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mygaurdian.domain.usecases.DeleteTeamUseCase
import com.example.mygaurdian.domain.usecases.GetTeamMembersUseCase
import com.example.mygaurdian.domain.usecases.InviteMemberUseCase

class TeamDetailViewModelFactory(
    private val membersUseCase: GetTeamMembersUseCase,
    private val deleteUseCase: DeleteTeamUseCase,
    private val inviteUseCase: InviteMemberUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(c: Class<T>): T {
        if (c.isAssignableFrom(TeamDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeamDetailViewModel(membersUseCase, deleteUseCase, inviteUseCase) as T
        }
        throw IllegalArgumentException("Unknown VM")
    }
}
