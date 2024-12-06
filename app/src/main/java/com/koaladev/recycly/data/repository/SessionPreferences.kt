package com.koaladev.recycly.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "session")

class SessionPreferences private constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {

    suspend fun saveSession(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[LOGIN_STATE] = true
        }
    }

    fun getSession(): Flow<UserSession> {
        return dataStore.data.map { preferences ->
            UserSession(
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[LOGIN_STATE] ?: false
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SessionPreferences? = null

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOGIN_STATE = booleanPreferencesKey("login_state")

        fun getInstance(context: Context): SessionPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SessionPreferences(context.dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}

data class UserSession(
    val token: String,
    val isLogin: Boolean
)