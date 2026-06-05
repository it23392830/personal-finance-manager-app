package com.example.financeflow.repository.notification

import com.example.financeflow.data.local.dao.NotificationDao
import com.example.financeflow.data.local.entity.NotificationEntity
import com.example.financeflow.model.FinanceNotification
import com.example.financeflow.model.FinanceNotificationType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Coordinates notification persistence between local Room storage and Firestore.
 *
 * Room is the source of truth for the Compose UI. Firestore mirroring keeps the
 * data ready for future cross-device sync without changing the screen API.
 */
@Singleton
class NotificationRepository @Inject constructor(
    private val notificationDao: NotificationDao,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth
) {
    private val currentUserId: String?
        get() = auth.currentUser?.uid

    /** Converts a Room entity to the UI/domain model. */
    private fun entityToDomain(entity: NotificationEntity): FinanceNotification {
        return FinanceNotification(
            id = entity.id,
            title = entity.title,
            message = entity.message,
            timestamp = entity.timestamp,
            type = entity.type,
            isRead = entity.isRead
        )
    }

    /** Converts a domain notification to a Room entity for the current user. */
    private fun domainToEntity(notification: FinanceNotification, userId: String): NotificationEntity {
        return NotificationEntity(
            id = notification.id,
            userId = userId,
            title = notification.title,
            message = notification.message,
            timestamp = notification.timestamp,
            type = notification.type,
            isRead = notification.isRead
        )
    }

    /** Returns the current user's Firestore notifications collection. */
    private fun notificationsCollection(userId: String) =
        firestore.collection("users").document(userId).collection("notifications")

    /** Saves a notification locally and mirrors it to Firestore. */
    suspend fun insertNotification(notification: FinanceNotification) {
        val userId = currentUserId ?: return
        val entity = domainToEntity(notification, userId)
        notificationDao.insert(entity)
        notificationsCollection(userId).document(notification.id).set(entity).await()
    }

    /** Streams all notifications for the signed-in user, newest first. */
    fun getAllNotifications(): Flow<List<FinanceNotification>> {
        val userId = currentUserId ?: return flowOf(emptyList())
        return notificationDao.getAllNotificationsForUser(userId).map { list ->
            list.map { entityToDomain(it) }
        }
    }

    /** Streams unread notifications for the signed-in user, newest first. */
    fun getUnreadNotifications(): Flow<List<FinanceNotification>> {
        val userId = currentUserId ?: return flowOf(emptyList())
        return notificationDao.getUnreadNotificationsForUser(userId).map { list ->
            list.map { entityToDomain(it) }
        }
    }

    /** Marks one notification as read locally and in Firestore. */
    suspend fun markAsRead(id: String) {
        val userId = currentUserId ?: return
        val local = notificationDao.getNotificationById(id)
        notificationDao.markAsRead(id)
        runCatching {
            val payload = mapOf(
                "id" to id,
                "userId" to userId,
                "title" to (local?.title ?: ""),
                "message" to (local?.message ?: ""),
                "timestamp" to (local?.timestamp ?: System.currentTimeMillis()),
                "type" to (local?.type ?: FinanceNotificationType.MISSED),
                "isRead" to true
            )
            notificationsCollection(userId).document(id).set(payload, SetOptions.merge()).await()
        }
    }

    /** Deletes one notification locally and in Firestore. */
    suspend fun deleteNotification(id: String) {
        val userId = currentUserId ?: return
        notificationDao.deleteNotificationById(id)
        runCatching {
            notificationsCollection(userId).document(id).delete().await()
        }
    }

    /** Streams the unread count for the Home screen badge. */
    fun getUnreadCount(): Flow<Int> {
        val userId = currentUserId ?: return flowOf(0)
        return notificationDao.getUnreadCountForUser(userId)
    }
}
