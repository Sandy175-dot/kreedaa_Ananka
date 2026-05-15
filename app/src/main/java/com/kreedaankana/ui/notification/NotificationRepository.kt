package com.kreedaankana.ui.notification

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class NotificationRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun getNotifications(): Flow<List<NotificationModel>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            trySend(emptyList())
            return@callbackFlow
        }

        val subscription = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    android.util.Log.e("NOTIFICATION_ERROR", "Error: ${error.message}")
                    trySend(emptyList())
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val notifications = snapshot.toObjects(NotificationModel::class.java)
                        .sortedByDescending { it.timestamp }
                    trySend(notifications)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun markAsRead(notificationId: String) {
        db.collection("notifications").document(notificationId)
            .update("isRead", true)
    }

    fun deleteNotification(notificationId: String) {
        db.collection("notifications").document(notificationId).delete()
    }

    fun getUnreadCount(): Flow<Int> = callbackFlow {
        val userId = auth.currentUser?.uid ?: ""
        if (userId.isEmpty()) {
            trySend(0)
            return@callbackFlow
        }

        val subscription = db.collection("notifications")
            .whereEqualTo("userId", userId)
            .whereEqualTo("isRead", false)
            .addSnapshotListener { snapshot, _ ->
                if (snapshot != null) {
                    trySend(snapshot.size())
                }
            }
        awaitClose { subscription.remove() }
    }

    fun sendNotification(title: String, message: String, type: String, targetUserId: String) {
        val notification = hashMapOf(
            "title" to title,
            "message" to message,
            "type" to type,
            "userId" to targetUserId,
            "timestamp" to com.google.firebase.Timestamp.now(),
            "isRead" to false
        )
        db.collection("notifications").add(notification)
    }
}
