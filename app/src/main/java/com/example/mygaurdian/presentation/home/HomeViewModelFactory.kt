package com.example.mygaurdian.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mygaurdian.domain.usecases.GetTeamsUseCase
import com.example.mygaurdian.domain.usecases.ToggleLocationSharingUseCase

class HomeViewModelFactory(
    private val getTeams: GetTeamsUseCase,
    private val toggleSharing: ToggleLocationSharingUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(getTeams, toggleSharing) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
