package com.example.mygaurdian.domain.model

data class SOSEvent(
    val id: String = "",
    val userId: String = "",
    val teamId: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val timestamp: Long = 0L
)
