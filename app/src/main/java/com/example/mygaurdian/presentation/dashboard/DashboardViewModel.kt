package com.example.mygaurdian.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygaurdian.domain.model.SOSEvent
import com.example.mygaurdian.domain.model.Team
import com.example.mygaurdian.domain.usecases.GetSOSHistoryUseCase
import com.example.mygaurdian.domain.usecases.GetTeamsUseCase
import com.example.mygaurdian.domain.usecases.SendSOSUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DashboardViewModel(
    private val sendSOS: SendSOSUseCase,
    private val getHistory: GetSOSHistoryUseCase,
    private val getTeams: GetTeamsUseCase
) : ViewModel() {

    private val _history = MutableStateFlow<List<SOSEvent>>(emptyList())
    val history: StateFlow<List<SOSEvent>> = _history

    private val _status = MutableStateFlow<String?>(null)
    val status: StateFlow<String?> = _status

    private val _teams = MutableStateFlow<List<Team>>(emptyList())
    val teams: StateFlow<List<Team>> = _teams
    init {
        loadHistory()
        loadTeams()
    }

    fun loadHistory() = viewModelScope.launch {
        _history.value = getHistory()
    }

    fun loadTeams() = viewModelScope.launch {
        _teams.value = getTeams()
    }

    fun triggerSOS(teamId: String, lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                sendSOS(teamId, lat, lng)
                _status.value = "Sent via FCM"
            } catch (_: Exception) {
                _status.value = "FCM failed"
            }
            loadHistory()
        }
    }

    fun clearStatus() {
        _status.value = null
    }
}
