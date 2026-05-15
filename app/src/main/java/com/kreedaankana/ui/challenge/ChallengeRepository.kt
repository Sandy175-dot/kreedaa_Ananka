package com.kreedaankana.ui.challenge

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.kreedaankana.ui.notification.NotificationRepository
import com.kreedaankana.ui.team.TeamModel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ChallengeRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val notificationRepository = NotificationRepository()

    fun getChallengesRealtime(): Flow<List<ChallengeModel>> = callbackFlow {
        val subscription = db.collection("challenges")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val challenges = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(ChallengeModel::class.java)?.copy(challengeId = doc.id)
                    }
                    trySend(challenges)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun createChallenge(challenge: ChallengeModel): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: ""
            val finalChallenge = challenge.copy(
                createdBy = userId,
                timestamp = com.google.firebase.Timestamp.now()
            )
            val docRef = db.collection("challenges").add(finalChallenge).await()
            
            // Notify Opponent
            notificationRepository.sendNotification(
                "New Challenge Received",
                "Your team has been challenged by ${challenge.challengerTeam} in ${challenge.sport}.",
                "challenge",
                challenge.opponentUserId
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateChallengeStatus(challengeId: String, status: String, challengerUserId: String, challengerTeam: String, opponentTeam: String): Boolean {
        return try {
            db.collection("challenges").document(challengeId)
                .update("status", status).await()
            
            // Notify Challenger
            notificationRepository.sendNotification(
                "Challenge $status",
                "Your challenge from $challengerTeam to $opponentTeam has been $status.",
                "challenge",
                challengerUserId
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getAllTeams(): List<TeamModel> {
        return try {
            db.collection("teams").get().await().toObjects(TeamModel::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }
}
