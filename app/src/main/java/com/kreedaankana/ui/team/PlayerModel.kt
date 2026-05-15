package com.kreedaankana.ui.team

import com.google.firebase.Timestamp

data class PlayerModel(
    val playerId: String = "",
    val teamId: String = "",
    val playerName: String = "",
    val role: String = "",
    val jerseyNumber: String = "",
    val matchesPlayed: Int = 0,
    val runs: Int = 0,
    val wickets: Int = 0,
    val strikeRate: Double = 0.0,
    val mvpCount: Int = 0,
    val createdBy: String = "",
    val timestamp: Timestamp? = null
)
