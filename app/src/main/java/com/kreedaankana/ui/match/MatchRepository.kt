package com.kreedaankana.ui.match

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.kreedaankana.ui.notification.NotificationRepository

class MatchRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val notificationRepository = NotificationRepository()

    fun getMatches(): Flow<List<MatchModel>> = callbackFlow {
        val subscription = db.collection("matches")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val matches = snapshot.toObjects(MatchModel::class.java)
                    trySend(matches)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun saveMatch(match: MatchModel): Boolean {
        return try {
            val userId = auth.currentUser?.uid ?: ""
            val scoreA = match.scoreA
            val scoreB = match.scoreB

            val (winner, result) = when {
                scoreA > scoreB -> match.teamA to "${match.teamA} Won"
                scoreB > scoreA -> match.teamB to "${match.teamB} Won"
                else -> "Draw" to "Match Drawn"
            }

            val finalMatch = match.copy(
                winner = winner,
                result = result,
                createdBy = userId,
                timestamp = com.google.firebase.Timestamp.now()
            )

            // 1. Save Match
            db.collection("matches").add(finalMatch).await()

            // Send Notification
            notificationRepository.sendNotification(
                "Match Recorded",
                "Match between ${match.teamA} and ${match.teamB} has been recorded.",
                "match",
                userId
            )

            // 2. Update Team Stats (Auto-update wins/losses)
            if (winner != "Draw") {
                val loser = if (winner == match.teamA) match.teamB else match.teamA
                
                updateTeamStat(winner, "wins")
                updateTeamStat(loser, "losses")
            } else {
                // If draw, we could update 'draws' if we want
                updateTeamStat(match.teamA, "draws")
                updateTeamStat(match.teamB, "draws")
            }

            true
        } catch (e: Exception) {
            android.util.Log.e("MATCH_REPO", "Error saving match: ${e.message}")
            false
        }
    }

    private suspend fun updateTeamStat(teamName: String, field: String) {
        try {
            val snapshot = db.collection("teams")
                .whereEqualTo("teamName", teamName)
                .limit(1)
                .get()
                .await()

            if (!snapshot.isEmpty) {
                val teamDocId = snapshot.documents[0].id
                db.collection("teams").document(teamDocId)
                    .update(field, FieldValue.increment(1))
                    .await()
            }
        } catch (e: Exception) {
            android.util.Log.e("MATCH_REPO", "Error updating team $teamName: ${e.message}")
        }
    }
}
