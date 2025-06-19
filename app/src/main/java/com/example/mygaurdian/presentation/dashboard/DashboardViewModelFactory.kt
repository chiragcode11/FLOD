package com.example.mygaurdian.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mygaurdian.domain.usecases.GetSOSHistoryUseCase
import com.example.mygaurdian.domain.usecases.GetTeamsUseCase
import com.example.mygaurdian.domain.usecases.SendSOSUseCase

class DashboardViewModelFactory(
    private val sendSOS: SendSOSUseCase,
    private val getHistory: GetSOSHistoryUseCase,
    private val getTeams: GetTeamsUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardViewModel(sendSOS, getHistory, getTeams) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
