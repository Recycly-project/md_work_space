package com.koaladev.recycly.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.response.RedeemResponse
import com.koaladev.recycly.data.response.RewardItem
import kotlinx.coroutines.launch

class PointViewModel(private val repository: RecyclyRepository) : ViewModel() {

    private val _rewards = MutableLiveData<List<RewardItem>>()
    val rewards: LiveData<List<RewardItem>> = _rewards

    private val _redeemResult = MutableLiveData<Result<RedeemResponse>>()
    val redeemResult: LiveData<Result<RedeemResponse>> = _redeemResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    fun getRewards(token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getRewards(token)
                _rewards.value = response.data
            } catch (e: Exception) {
                _error.value = "Gagal memuat reward: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun redeemReward(userId: String, rewardId: String, token: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.redeemReward(userId, rewardId, token)
                _redeemResult.value = Result.success(response)
            } catch (e: Exception) {
                _redeemResult.value = Result.failure(e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}