package com.kreedaankana.ui.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreedaankana.ui.team.TeamModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChallengeViewModel : ViewModel() {

    private val repository = ChallengeRepository()

    private val _challenges = MutableLiveData<List<ChallengeModel>>()
    val challenges: LiveData<List<ChallengeModel>> = _challenges

    private val _teams = MutableLiveData<List<TeamModel>>()
    val teams: LiveData<List<TeamModel>> = _teams

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchChallenges()
        fetchTeams()
    }

    private fun fetchChallenges() {
        _isLoading.value = true
        viewModelScope.launch {
            repository.getChallengesRealtime().collect {
                _challenges.value = it
                _isLoading.value = false
            }
        }
    }

    private fun fetchTeams() {
        viewModelScope.launch {
            _teams.value = repository.getAllTeams()
        }
    }

    fun createChallenge(challenge: ChallengeModel, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.createChallenge(challenge)
            onResult(success)
        }
    }

    fun updateChallengeStatus(challengeId: String, status: String, challengerUserId: String, challengerTeam: String, opponentTeam: String) {
        viewModelScope.launch {
            repository.updateChallengeStatus(challengeId, status, challengerUserId, challengerTeam, opponentTeam)
        }
    }
}
