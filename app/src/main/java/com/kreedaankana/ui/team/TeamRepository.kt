package com.kreedaankana.ui.team

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import com.kreedaankana.ui.notification.NotificationRepository

class TeamRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val notificationRepository = NotificationRepository()

    fun getTeams(): Flow<List<TeamModel>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }
        
        val subscription = db.collection("teams")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("FIRESTORE_ERROR", "Error fetching teams: ${error.message}")
                    trySend(emptyList()) // Send empty list to stop loading
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val teams = snapshot.toObjects(TeamModel::class.java)
                    trySend(teams)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun addTeam(team: TeamModel, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: ""
        val teamWithUser = team.copy(userId = userId)
        
        db.collection("teams")
            .add(teamWithUser)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val id = task.result.id
                    db.collection("teams").document(id).update("teamId", id)
                    notificationRepository.sendNotification(
                        "Team Created",
                        "Your team '${team.teamName}' has been successfully created.",
                        "team",
                        userId
                    )
                }
                onComplete(task.isSuccessful)
            }
    }

    // --- PLAYER MANAGEMENT ---

    fun getPlayers(teamId: String): Flow<List<PlayerModel>> = callbackFlow {
        if (teamId.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val subscription = db.collection("teams").document(teamId)
            .collection("players")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val players = snapshot.toObjects(PlayerModel::class.java)
                    trySend(players)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun addPlayer(teamId: String, player: PlayerModel, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: ""
        val playerWithUser = player.copy(createdBy = userId, teamId = teamId)
        
        val playerRef = db.collection("teams").document(teamId).collection("players").document()
        val finalPlayer = playerWithUser.copy(playerId = playerRef.id)
        
        playerRef.set(finalPlayer)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Update player count in team
                    db.collection("teams").document(teamId)
                        .update("playersCount", com.google.firebase.firestore.FieldValue.increment(1))
                }
                onComplete(task.isSuccessful)
            }
    }

    fun deletePlayer(teamId: String, playerId: String, onComplete: (Boolean) -> Unit) {
        db.collection("teams").document(teamId)
            .collection("players").document(playerId)
            .delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    db.collection("teams").document(teamId)
                        .update("playersCount", com.google.firebase.firestore.FieldValue.increment(-1))
                }
                onComplete(task.isSuccessful)
            }
    }

    fun updatePlayer(teamId: String, player: PlayerModel, onComplete: (Boolean) -> Unit) {
        db.collection("teams").document(teamId)
            .collection("players").document(player.playerId)
            .set(player)
            .addOnCompleteListener { onComplete(it.isSuccessful) }
    }
}
