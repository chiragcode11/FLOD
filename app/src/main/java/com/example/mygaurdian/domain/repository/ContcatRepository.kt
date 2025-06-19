package com.example.mygaurdian.domain.repository

import com.example.mygaurdian.domain.model.Contact

interface ContactRepository {
    suspend fun getContacts(): List<Contact>
}
