package com.kreedaankana.ui.booking

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import com.kreedaankana.ui.notification.NotificationRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class BookingRepository {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val bookingsCollection = db.collection("bookings")
    private val notificationRepository = NotificationRepository()

    suspend fun createBooking(sport: String, slotTime: String, date: String): Result<Boolean> {
        return try {
            val userId = auth.currentUser?.uid ?: return Result.failure(Exception("User not logged in"))

            // DUPLICATE PREVENTION: Check if slot is already taken
            val existing = bookingsCollection
                .whereEqualTo("date", date)
                .whereEqualTo("slotTime", slotTime)
                .whereEqualTo("status", "Confirmed")
                .get()
                .await()
            
            if (!existing.isEmpty) {
                return Result.failure(Exception("This slot was just booked by someone else!"))
            }

            val booking = hashMapOf(
                "sport" to sport,
                "slotTime" to slotTime,
                "date" to date,
                "userId" to userId,
                "status" to "Confirmed",
                "groundName" to "Arena Central", // Default for now
                "timestamp" to Timestamp.now()
            )

            bookingsCollection.add(booking).await()
            
            // Send In-App Notification
            notificationRepository.sendNotification(
                "Booking Confirmed",
                "Your slot for $sport at $slotTime on $date is reserved.",
                "booking",
                userId
            )

            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getBookedSlotsForDate(date: String): Flow<List<String>> = callbackFlow {
        val subscription = bookingsCollection
            .whereEqualTo("date", date)
            .whereEqualTo("status", "Confirmed")
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot != null) {
                    val bookedTimes = snapshot.documents.mapNotNull { it.getString("slotTime") }
                    trySend(bookedTimes)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getBookingsRealtime(): Flow<List<BookingModel>> = callbackFlow {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            close(Exception("User not authenticated"))
            return@callbackFlow
        }

        val listener = bookingsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                if (snapshot != null) {
                    val bookings = snapshot.documents.mapNotNull { doc ->
                        val sport = doc.getString("sport") ?: ""
                        val date = doc.getString("date") ?: ""
                        val time = doc.getString("slotTime") ?: ""
                        val status = doc.getString("status") ?: ""
                        BookingModel(doc.id, sport, time, date, userId, null)
                    }
                    trySend(bookings)
                }
            }

        awaitClose { listener.remove() }
    }
}
