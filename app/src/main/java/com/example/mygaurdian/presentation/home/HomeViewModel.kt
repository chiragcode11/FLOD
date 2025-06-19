package com.example.mygaurdian.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygaurdian.domain.model.Team
import com.example.mygaurdian.domain.usecases.GetTeamsUseCase
import com.example.mygaurdian.domain.usecases.ToggleLocationSharingUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTeams: GetTeamsUseCase,
    private val toggleSharing: ToggleLocationSharingUseCase
) : ViewModel() {

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams

    init { loadTeams() }

    private fun loadTeams() = viewModelScope.launch {
        _teams.value = getTeams()
    }

    fun onToggleSharing(team: Team, isSharing: Boolean) = viewModelScope.launch {
        toggleSharing(team.id, isSharing)
        _teams.value = getTeams()
    }
}
