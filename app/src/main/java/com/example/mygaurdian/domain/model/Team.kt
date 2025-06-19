package com.example.mygaurdian.domain.model

data class Team(
    val id: String = "",
    val name: String = "",
    val memberIds: List<String> = emptyList(),
    val sharing: Boolean = false
)
