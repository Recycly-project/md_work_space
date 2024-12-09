package com.koaladev.recycly.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<androidx.datastore.preferences.core.Preferences> by preferencesDataStore(name = "session")

class SessionPreferences private constructor(private val dataStore: DataStore<androidx.datastore.preferences.core.Preferences>) {

    suspend fun saveSession(token: String, userId: String, userEmail: String, userFullName: String, isAdmin: Boolean) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
            preferences[LOGIN_STATE] = true
            preferences[USER_ID] = userId
            preferences[USER_EMAIL] = userEmail
            preferences[USER_FULLNAME] = userFullName
        }
    }

    fun getSession(): Flow<UserSession> {
        return dataStore.data.map { preferences ->
            UserSession(
                token = preferences[TOKEN_KEY] ?: "",
                isLogin = preferences[LOGIN_STATE] ?: false,
                userId = preferences[USER_ID]?: "",
                userEmail = preferences[USER_EMAIL]?: "",
                userFullName = preferences[USER_FULLNAME]?: "",
                isAdmin = preferences[USER_ADMIN]?: false,
            )
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    suspend fun getUserId(): String {
        return dataStore.data.first()[USER_ID] ?: ""
    }

    suspend fun getToken(): String {
        return dataStore.data.first()[TOKEN_KEY] ?: ""
    }

    companion object {
        @Volatile
        private var INSTANCE: SessionPreferences? = null

        private val TOKEN_KEY = stringPreferencesKey("token")
        private val LOGIN_STATE = booleanPreferencesKey("login_state")
        private val USER_ID = stringPreferencesKey("user_id")
        private val USER_EMAIL = stringPreferencesKey("user_email")
        private val USER_FULLNAME = stringPreferencesKey("user_fullname")
        private val USER_ADMIN = booleanPreferencesKey("user_admin")

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
    val isLogin: Boolean,
    val userId: String,
    val userEmail: String,
    val userFullName: String,
    val isAdmin: Boolean
)