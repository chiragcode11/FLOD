package com.example.mygaurdian.data.remote

import com.example.mygaurdian.domain.model.CommunityReport
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlin.math.*

class CommunityRemoteDataSource {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val userId: String
        get() = auth.currentUser?.uid ?: throw IllegalStateException("Not signed in")

    suspend fun getReportsNearby(
        centerLat: Double, centerLng: Double, radiusM: Double
    ): List<CommunityReport> {
        val snap = db.collection("communityReports")
            .get()
            .await()

        return snap.documents.mapNotNull { doc ->
            val d = doc.data ?: return@mapNotNull null
            val report = CommunityReport(
                id = doc.id,
                reporterId = d["reporterId"] as String,
                lat = (d["lat"] as Number).toDouble(),
                lng = (d["lng"] as Number).toDouble(),
                category = d["category"] as String,
                severity = (d["severity"] as Number).toInt(),
                description = d["description"] as String,
                imageUrls = d["imageUrls"] as? List<String> ?: emptyList(),
                timestamp = d["timestamp"] as Long,
                upvotes = d["upvotes"] as? List<String> ?: emptyList()
            )
            if (haversine(centerLat, centerLng, report.lat, report.lng) <= radiusM) {
                report
            } else null
        }
    }

    suspend fun addReport(report: CommunityReport): String {
        val payload = mapOf(
            "reporterId"  to userId,
            "lat"         to report.lat,
            "lng"         to report.lng,
            "category"    to report.category,
            "severity"    to report.severity,
            "description" to report.description,
            "imageUrls"   to report.imageUrls,
            "timestamp"   to System.currentTimeMillis(),
            "upvotes"     to listOf<String>()
        )
        val docRef = db.collection("communityReports")
            .add(payload)
            .await()
        return docRef.id
    }

    suspend fun upvoteReport(reportId: String, userId: String) {
        db.collection("communityReports")
            .document(reportId)
            .update("upvotes", com.google.firebase.firestore.FieldValue.arrayUnion(userId))
            .await()
    }

    private fun haversine(
        lat1: Double, lon1: Double, lat2: Double, lon2: Double
    ): Double {
        val R = 6371000.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2.0) +
                cos(Math.toRadians(lat1)) *
                cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2.0)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return R * c
    }
}
