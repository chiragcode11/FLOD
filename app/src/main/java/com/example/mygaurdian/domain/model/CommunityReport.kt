package com.example.mygaurdian.domain.model

data class CommunityReport(
    val id: String = "",
    val reporterId: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val category: String = "",
    val severity: Int = 1,
    val description: String = "",
    val imageUrls: List<String> = emptyList(),
    val timestamp: Long = 0L,
    val upvotes: List<String> = emptyList()
)
