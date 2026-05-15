package com.kreedaankana.ui.booking

import com.google.firebase.Timestamp

data class BookingModel(
    val id: String = "",
    val sport: String = "",
    val date: String = "",
    val time: String = "",
    val userId: String = "",
    val timestamp: Timestamp? = null
)
