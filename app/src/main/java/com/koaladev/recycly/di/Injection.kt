package com.koaladev.recycly.di

import android.content.Context
import com.koaladev.recycly.data.repository.RecyclyRepository
import com.koaladev.recycly.data.retrofit.ApiService
import retrofit2.Retrofit

object Injection {
    private fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    fun provideRecyclyRepository(context: Context, retrofit: Retrofit): RecyclyRepository {
        val apiService = provideApiService(retrofit)
        return RecyclyRepository.getInstance(apiService)
    }
}