package com.example.mygaurdian.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mygaurdian.domain.model.Contact
import com.example.mygaurdian.domain.usecases.AddTeamUseCase
import com.example.mygaurdian.domain.usecases.GetContactsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CreateTeamViewModel(
    private val getContacts: GetContactsUseCase,
    private val addTeam: AddTeamUseCase
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts: StateFlow<List<Contact>> = _contacts

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    init { loadContacts() }

    private fun loadContacts() = viewModelScope.launch {
        _contacts.value = getContacts()
    }

    fun toggleContact(c: Contact) {
        _contacts.value = _contacts.value.map {
            if (it.id == c.id) it.copy(isSelected = c.isSelected) else it
        }
    }

    fun saveTeam(name: String) = viewModelScope.launch {
        _isSaving.value = true
        val selected = _contacts.value.filter { it.isSelected }.map { it.id }
        addTeam(name, selected)
        _isSaving.value = false
    }
}
