package com.example.medical_schedule_app.data.repositories

import com.example.medical_schedule_app.data.api.ApiService
import com.example.medical_schedule_app.data.models.Role
import com.example.medical_schedule_app.data.models.requests.LoginRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getRoles(): List<Role> = withContext(Dispatchers.IO) {
        // Filter out HeadOffice role as per requirements
        val response = apiService.getRoles()
        response.filter { it.name != "HeadOffice" }
    }

    suspend fun login(email: String, password: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.login(LoginRequest(email, password))
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(email: String, roleId: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val response = when (roleId) {
                2 -> apiService.signupBranch(email) // Branch/Admin
                4 -> apiService.signupDoctor(email) // Doctor
                5 -> apiService.signupReceptionist(email) // Receptionist
                else -> throw IllegalArgumentException("Invalid role ID for signup")
            }
            Result.success(response.token)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}