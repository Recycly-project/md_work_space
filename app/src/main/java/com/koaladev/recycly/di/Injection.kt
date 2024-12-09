package com.koaladev.recycly.di

import android.content.Context
import com.koaladev.recycly.data.pref.UserPreference
import com.koaladev.recycly.data.pref.dataStore
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.repository.WasteRepository
import com.koaladev.recycly.data.retrofit.ApiConfig
import com.koaladev.recycly.data.retrofit.ApiConfigAuth

object Injection {
    fun provideRecyclyRepository(context: Context): RecyclyRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return RecyclyRepository.getInstance(pref, ApiConfigAuth.getApiService())
    }

    fun provideWasteRepository(context: Context): WasteRepository {
        return WasteRepository(ApiConfig.getApiService())
    }
}