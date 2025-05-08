package com.example.medical_schedule_app.utils

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActualSessionManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "AppPrefs_MSA"
        private const val USER_TOKEN = "user_token"
        private const val USER_ID = "user_id"
    }

    fun saveAuthToken(token: String) {
        prefs.edit().putString(USER_TOKEN, token).apply()
    }

    fun fetchAuthToken(): String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun saveUserId(userId: Int) {
        prefs.edit().putInt(USER_ID, userId).apply()
    }

    fun fetchUserId(): Int? {
        val id = prefs.getInt(USER_ID, -1)
        return if (id == -1) null else id
    }

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
