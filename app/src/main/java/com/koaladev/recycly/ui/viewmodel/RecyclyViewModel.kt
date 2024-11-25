package com.koaladev.recycly.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RecyclyViewModel: ViewModel() {

    // Get
    private var _points: MutableLiveData<Int> = MutableLiveData()
    val points: LiveData<Int> get() = _points

    fun setPoints(point: Int) {
        _points.value = point
    }

    fun addPoints(point: Int) {
        val currentPoints = _points.value?: 0
        _points.value = currentPoints + point
    }

}