package com.kreedaankana.ui.leaderboard

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class LeaderboardRepository {
    private val db = FirebaseFirestore.getInstance()
    private val teamsCollection = db.collection("teams")

    fun getLeaderboardRealtime(): Flow<List<LeaderboardModel>> = callbackFlow {
        val listener = teamsCollection
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val leaderboard = snapshot.documents.mapNotNull { doc ->
                        val teamName = doc.getString("teamName") ?: "Unknown"
                        val captain = doc.getString("captain") ?: "Unknown"
                        val wins = doc.getLong("wins")?.toInt() ?: 0
                        val draws = doc.getLong("draws")?.toInt() ?: 0
                        val losses = doc.getLong("losses")?.toInt() ?: 0
                        
                        // POINTS SYSTEM: Win = 3, Draw = 1, Loss = 0
                        val points = (wins * 3) + (draws * 1)
                        
                        LeaderboardModel(
                            teamName = teamName,
                            captain = captain,
                            points = points,
                            wins = wins,
                            draws = draws,
                            losses = losses,
                            matchesPlayed = wins + draws + losses
                        )
                    }.sortedWith(
                        compareByDescending<LeaderboardModel> { it.points }
                            .thenByDescending { it.wins }
                    )
                    
                    trySend(leaderboard)
                }
            }

        awaitClose { listener.remove() }
    }
}
