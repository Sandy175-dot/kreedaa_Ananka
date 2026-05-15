package com.kreedaankana.ui.match

import com.google.firebase.Timestamp

data class MatchModel(
    val matchId: String = "",
    val teamA: String = "",
    val teamB: String = "",
    val teamAId: String = "", // Added for internal stat updates
    val teamBId: String = "", // Added for internal stat updates
    val sport: String = "",
    val scoreA: Int = 0,
    val scoreB: Int = 0,
    val winner: String = "",
    val result: String = "",
    val matchDate: String = "",
    val matchTime: String = "",
    val createdBy: String = "",
    val timestamp: Timestamp? = null
)
