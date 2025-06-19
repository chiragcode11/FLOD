package com.example.mygaurdian.data.remote

import com.example.mygaurdian.domain.model.SOSEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

class SOSRemoteDataSource {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val userId: String
        get() = auth.currentUser?.uid
            ?: throw IllegalStateException("Not signed in")

    suspend fun sendSOS(teamId: String, lat: Double, lng: Double) {
        val payload = mapOf(
            "userId" to userId,
            "teamId" to teamId,
            "lat" to lat,
            "lng" to lng,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("sosEvents")
            .add(payload)
            .await()
    }

    /**
     * Retrieves SOS history, ordered by timestamp descending if possible.
     * If Firestore complains about a missing index, fall back to an unordered
     * query and sort on the client.
     */
    suspend fun getSOSHistory(): List<SOSEvent> {
        return try {
            // Primary: server‐side ordering (needs composite index)
            db.collection("sosEvents")
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
                .get()
                .await()
                .toSOSEventList()
        } catch (e: FirebaseFirestoreException) {
            if (e.code == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                // Missing index → fallback
                val snapshot = db.collection("sosEvents")
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()
                snapshot.toSOSEventList()
                    .sortedByDescending { it.timestamp }
            } else {
                throw e
            }
        }
    }

    private fun com.google.firebase.firestore.QuerySnapshot.toSOSEventList(): List<SOSEvent> {
        return documents.map { doc ->
            val data = doc.data ?: emptyMap<String, Any>()
            SOSEvent(
                id = doc.id,
                userId = data["userId"] as String,
                teamId = data["teamId"] as String,
                lat = (data["lat"] as Number).toDouble(),
                lng = (data["lng"] as Number).toDouble(),
                timestamp = data["timestamp"] as Long
            )
        }
    }
}
