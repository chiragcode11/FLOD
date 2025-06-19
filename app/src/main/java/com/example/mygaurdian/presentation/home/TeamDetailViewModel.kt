package com.example.mygaurdian.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygaurdian.domain.model.User
import com.example.mygaurdian.domain.usecases.DeleteTeamUseCase
import com.example.mygaurdian.domain.usecases.GetTeamMembersUseCase
import com.example.mygaurdian.domain.usecases.InviteMemberUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class TeamDetailViewModel(
    val getMembers: GetTeamMembersUseCase,
    private val deleteTeam: DeleteTeamUseCase,
    private val inviteMember: InviteMemberUseCase
) : ViewModel() {

    private val _members = MutableStateFlow<List<User>>(emptyList())
    val members: StateFlow<List<User>> = _members

    val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid
        ?: throw IllegalStateException("User must be signed in")

    fun loadMembers(teamId: String) = viewModelScope.launch {
        _members.value = getMembers(teamId)
    }

    fun onDelete(teamId: String, onDone: () -> Unit) = viewModelScope.launch {
        deleteTeam(teamId)
        onDone()
    }

    fun onInvite(teamId: String, memberId: String, teamName: String) = viewModelScope.launch {
        inviteMember(teamId, memberId, teamName)
    }
}
