package com.example.financeflow.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.financeflow.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Database operations for notification records.
 *
 * All list queries sort by timestamp descending so the newest notification is
 * always displayed at the top of NotificationScreen.
 */
@Dao
interface NotificationDao {

    /** Inserts or replaces one notification. */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: NotificationEntity)

    /** Deletes a notification entity when the caller already has the record. */
    @Delete
    suspend fun delete(notification: NotificationEntity)

    /** Updates a notification entity, usually after changing read state. */
    @Update
    suspend fun update(notification: NotificationEntity)

    /** Returns every notification across users, newest first. */
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NotificationEntity>>

    /** Returns unread notifications across users, newest first. */
    @Query("SELECT * FROM notifications WHERE isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadNotifications(): Flow<List<NotificationEntity>>

    /** Returns unread notification count across users. */
    @Query("SELECT COUNT(*) FROM notifications WHERE isRead = 0")
    fun getUnreadCount(): Flow<Int>

    /** Returns current user's notifications, newest first. */
    @Query("SELECT * FROM notifications WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    /** Returns current user's unread notifications, newest first. */
    @Query("SELECT * FROM notifications WHERE userId = :userId AND isRead = 0 ORDER BY timestamp DESC")
    fun getUnreadNotificationsForUser(userId: String): Flow<List<NotificationEntity>>

    /** Returns current user's unread count for the Home badge. */
    @Query("SELECT COUNT(*) FROM notifications WHERE userId = :userId AND isRead = 0")
    fun getUnreadCountForUser(userId: String): Flow<Int>

    /** Finds one notification by id. */
    @Query("SELECT * FROM notifications WHERE id = :id LIMIT 1")
    suspend fun getNotificationById(id: String): NotificationEntity?

    /** Marks one notification as read. */
    @Query("UPDATE notifications SET isRead = 1 WHERE id = :id")
    suspend fun markAsRead(id: String)

    /** Deletes one notification by id. */
    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: String)
}
