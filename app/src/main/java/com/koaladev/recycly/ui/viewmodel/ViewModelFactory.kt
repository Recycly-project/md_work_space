package com.koaladev.recycly.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.di.Injection

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

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRecyclyRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}