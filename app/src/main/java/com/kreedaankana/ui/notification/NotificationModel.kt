package com.kreedaankana.ui.notification

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class NotificationModel(
    @DocumentId
    val id: String = "",
    val title: String = "",
    val message: String = "",
    val type: String = "",
    val userId: String = "",
    val timestamp: Timestamp? = null,
    val isRead: Boolean = false
)
