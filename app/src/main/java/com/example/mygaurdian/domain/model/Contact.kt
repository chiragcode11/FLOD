package com.example.mygaurdian.domain.model

data class Contact(
    val id: String,
    val name: String,
    val phone: String,
    var isSelected: Boolean = false
)
