package com.koaladev.recycly.di

import android.content.Context
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.retrofit.ApiConfig
import com.koaladev.recycly.data.retrofit.ApiService
import retrofit2.Retrofit

object Injection {
    fun provideRecyclyRepository(context: Context): RecyclyRepository {
        val apiService = ApiConfig.getApiService()
        return RecyclyRepository.getInstance(apiService)
    }
}