package com.koaladev.recycly.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.pref.dataStore
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.WasteRepository
import com.koaladev.recycly.di.Injection

class ViewModelFactory(
    private val application: Application,
    private val recyclyRepository: RecyclyRepository,
    private val wasteRepository: WasteRepository
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RecyclyViewModel::class.java) -> {
                RecyclyViewModel(application, recyclyRepository, wasteRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(recyclyRepository, UserPreference.getInstance(application.dataStore)) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(recyclyRepository) as T
            }
            modelClass.isAssignableFrom(PointViewModel::class.java) -> {
                PointViewModel(recyclyRepository) as T
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
                    val application = context.applicationContext as Application
                    INSTANCE = ViewModelFactory(
                        application,
                        Injection.provideRecyclyRepository(context),
                        Injection.provideWasteRepository(context)
                    )
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}