package com.kreedaankana.ui.leaderboard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class LeaderboardViewModel : ViewModel() {
    private val repository = LeaderboardRepository()

    private val _leaderboard = MutableLiveData<List<LeaderboardModel>>()
    val leaderboard: LiveData<List<LeaderboardModel>> = _leaderboard

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        observeLeaderboard()
    }

    private fun observeLeaderboard() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getLeaderboardRealtime()
                .catch { e -> 
                    _isLoading.value = false
                }
                .collect { list ->
                    _leaderboard.value = list
                    _isLoading.value = false
                }
        }
    }
}
