package com.example.medical_schedule_app.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.medical_schedule_app.data.models.Role
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MedicareAppPrefs", Context.MODE_PRIVATE)
    private val editor: SharedPreferences.Editor = sharedPreferences.edit()

    companion object {
        private const val KEY_TOKEN = "user_token"
        private const val KEY_ROLE_ID = "user_role_id"
        private const val KEY_ROLE_NAME = "user_role_name"
    }

    fun saveAuthToken(token: String) {
        editor.putString(KEY_TOKEN, token)
        editor.apply()
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_TOKEN, null)
    }

    fun saveUserRole(role: Role) {
        editor.putInt(KEY_ROLE_ID, role.role_id)
        editor.putString(KEY_ROLE_NAME, role.name)
        editor.apply()
    }

    fun getUserRoleId(): Int {
        return sharedPreferences.getInt(KEY_ROLE_ID, -1)
    }

    fun getUserRoleName(): String? {
        return sharedPreferences.getString(KEY_ROLE_NAME, null)
    }

    fun isLoggedIn(): Boolean {
        return getAuthToken() != null
    }

    fun getRoleId(): Int {
        return getUserRoleId()
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }
}