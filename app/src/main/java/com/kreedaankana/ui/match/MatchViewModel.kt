package com.kreedaankana.ui.match

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class MatchViewModel : ViewModel() {

    private val repository = MatchRepository()

    private val _matches = MutableLiveData<List<MatchModel>>()
    val matches: LiveData<List<MatchModel>> = _matches

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchMatches()
    }

    private fun fetchMatches() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getMatches().collect {
                _matches.value = it
                _isLoading.value = false
            }
        }
    }

    fun saveMatch(match: MatchModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.saveMatch(match)
            onResult(success)
        }
    }
}
