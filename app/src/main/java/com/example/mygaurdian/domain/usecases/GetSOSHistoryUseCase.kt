package com.example.mygaurdian.domain.usecases

import com.example.mygaurdian.domain.model.SOSEvent
import com.example.mygaurdian.domain.repository.SOSRepository

class GetSOSHistoryUseCase(private val repo: SOSRepository) {
    suspend operator fun invoke(): List<SOSEvent> = repo.getSOSHistory()
}
