package com.kreedaankana.ui.leaderboard

data class LeaderboardModel(
    val teamName: String = "",
    val captain: String = "",
    val points: Int = 0,
    val wins: Int = 0,
    val draws: Int = 0,
    val losses: Int = 0,
    val matchesPlayed: Int = 0
)
