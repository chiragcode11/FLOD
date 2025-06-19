package com.example.mygaurdian.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mygaurdian.domain.usecases.AddTeamUseCase
import com.example.mygaurdian.domain.usecases.GetContactsUseCase

class CreateTeamViewModelFactory(
    private val getContacts: GetContactsUseCase,
    private val addTeam: AddTeamUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CreateTeamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateTeamViewModel(getContacts, addTeam) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
