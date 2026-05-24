package com.example.financeflow.viewmodel.notification

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeflow.model.FinanceNotification
import com.example.financeflow.repository.notification.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for NotificationScreen and the Home notification badge.
 */
@HiltViewModel
class NotificationViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _notifications = MutableStateFlow<List<FinanceNotification>>(emptyList())
    val notifications: StateFlow<List<FinanceNotification>> = _notifications.asStateFlow()

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount.asStateFlow()

    init {
        loadNotifications()
        getUnreadCount()
    }

    /** Starts collecting all notifications from Room. */
    fun loadNotifications() {
        viewModelScope.launch {
            notificationRepository.getAllNotifications().collect { list ->
                _notifications.value = list
            }
        }
    }

    /** Marks one notification as read. */
    fun markAsRead(id: String) {
        viewModelScope.launch {
            notificationRepository.markAsRead(id)
        }
    }

    /** Deletes one notification. */
    fun deleteNotification(id: String) {
        viewModelScope.launch {
            notificationRepository.deleteNotification(id)
        }
    }

    /** Starts collecting the unread notification count for the badge. */
    fun getUnreadCount() {
        viewModelScope.launch {
            notificationRepository.getUnreadCount().collect { count ->
                _unreadCount.value = count
            }
        }
    }
}
