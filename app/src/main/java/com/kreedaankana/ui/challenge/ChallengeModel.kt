package com.kreedaankana.ui.challenge

import com.google.firebase.Timestamp

data class ChallengeModel(
    val challengeId: String = "",
    val challengerTeam: String = "",
    val opponentTeam: String = "",
    val opponentUserId: String = "", // Added to know who to notify
    val sport: String = "",
    val matchDate: String = "",
    val venue: String = "",
    val message: String = "",
    val status: String = "Pending", // Pending, Accepted, Rejected
    val createdBy: String = "",
    val timestamp: Timestamp? = null
)
