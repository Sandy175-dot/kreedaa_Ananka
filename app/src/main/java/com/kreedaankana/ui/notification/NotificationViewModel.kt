package com.kreedaankana.ui.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class NotificationViewModel : ViewModel() {

    private val repository = NotificationRepository()

    private val _notifications = MutableLiveData<List<NotificationModel>>()
    val notifications: LiveData<List<NotificationModel>> = _notifications

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _unreadCount = MutableLiveData<Int>()
    val unreadCount: LiveData<Int> = _unreadCount

    init {
        fetchNotifications()
        fetchUnreadCount()
    }

    private fun fetchNotifications() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getNotifications().collect {
                _notifications.value = it
                _isLoading.value = false
            }
        }
    }

    private fun fetchUnreadCount() {
        viewModelScope.launch {
            repository.getUnreadCount().collect {
                _unreadCount.value = it
            }
        }
    }

    fun markAsRead(notificationId: String) {
        repository.markAsRead(notificationId)
    }

    fun deleteNotification(notificationId: String) {
        repository.deleteNotification(notificationId)
    }
}
