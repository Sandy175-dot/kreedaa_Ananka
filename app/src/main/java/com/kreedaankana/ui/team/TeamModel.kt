package com.kreedaankana.ui.team

import com.google.firebase.Timestamp

data class TeamModel(
    val teamId: String = "",
    val teamName: String = "",
    val captain: String = "",
    val playersCount: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val draw: Int = 0,
    val avgScore: Int = 0,
    val recentForm: String = "---", // e.g., "WLW"
    val userId: String = "",
    val timestamp: Timestamp? = null
)
