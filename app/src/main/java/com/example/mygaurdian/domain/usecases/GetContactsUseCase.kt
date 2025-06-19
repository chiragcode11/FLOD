package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.model.Contact
import com.example.mygaurdian.domain.repository.ContactRepository

class GetContactsUseCase(private val repo: ContactRepository) {
    suspend operator fun invoke(): List<Contact> = repo.getContacts()
}
