package com.example.mygaurdian.data.remote

import com.example.mygaurdian.domain.model.Team
import com.example.mygaurdian.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TeamRemoteDataSource(
    private val firestore: FirebaseFirestore
) {
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    suspend fun getTeams(): List<Team> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("teams")
            .get().await()
        return snapshot.documents.map {
            it.toObject(Team::class.java)!!.copy(id = it.id)
        }
    }

    suspend fun toggleSharing(teamId: String, sharing: Boolean) {
        firestore.collection("users")
            .document(userId)
            .collection("teams")
            .document(teamId)
            .update("sharing", sharing)
            .await()
    }
    suspend fun addTeam(name: String, memberIds: List<String>) {
        val data = mapOf(
            "name" to name,
            "memberIds" to memberIds,
            "sharing" to false
        )
        firestore.collection("users")
            .document(userId)
            .collection("teams")
            .add(data)
            .await()
    }
    suspend fun deleteTeam(teamId: String) {
        firestore.collection("users")
            .document(userId)
            .collection("teams")
            .document(teamId)
            .delete()
            .await()
    }

    suspend fun getTeamMembers(teamId: String): List<User> {
        val teamDoc = firestore.collection("users")
            .document(userId)
            .collection("teams")
            .document(teamId)
            .get().await()
        val ids = teamDoc.get("memberIds") as? List<String> ?: emptyList()
        return ids.mapNotNull { memberId ->
            val u = firestore.collection("users")
                .document(memberId)
                .get().await()
            u.toObject(User::class.java)?.copy(id = memberId)
        }
    }

    suspend fun inviteMember(teamId: String, memberId: String, teamName: String) {
        val payload = mapOf(
            "teamId" to teamId,
            "teamName" to teamName,
            "inviterId" to userId,
            "timestamp" to System.currentTimeMillis()
        )
        firestore.collection("users")
            .document(memberId)
            .collection("invites")
            .document(teamId)
            .set(payload)
            .await()
    }

    suspend fun getTeamContactIds(teamId: String): List<String> {
        val doc = firestore.collection("users")
            .document(userId)
            .collection("teams")
            .document(teamId)
            .get().await()
        return doc.get("memberIds") as? List<String> ?: emptyList()
    }
}

