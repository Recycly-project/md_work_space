package com.koaladev.recycly.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.koaladev.recycly.data.repository.RecyclyRepository

class ViewModelFactory(
    private val recyclyRepository: RecyclyRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RecyclyViewModel::class.java) -> {
                RecyclyViewModel(recyclyRepository) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }
}