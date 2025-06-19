package com.example.mygaurdian.data.repository

import com.example.mygaurdian.data.local.ContactLocalDataSource
import com.example.mygaurdian.domain.model.Contact
import com.example.mygaurdian.domain.repository.ContactRepository

class ContactRepositoryImpl(
    private val local: ContactLocalDataSource
) : ContactRepository {
    override suspend fun getContacts(): List<Contact> = local.fetchContacts()
}
