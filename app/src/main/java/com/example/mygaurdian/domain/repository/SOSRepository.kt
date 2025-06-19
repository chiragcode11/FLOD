package com.example.mygaurdian.domain.repository

import com.example.mygaurdian.domain.model.SOSEvent

interface SOSRepository {
    suspend fun sendSOS(teamId: String, lat: Double, lng: Double)
    suspend fun getSOSHistory(): List<SOSEvent>
}
