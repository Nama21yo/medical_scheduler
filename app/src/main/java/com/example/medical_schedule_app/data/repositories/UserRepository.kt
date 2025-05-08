package com.example.medical_schedule_app.data.repositories

import com.example.medical_schedule_app.data.api.ApiService
import com.example.medical_schedule_app.data.models.Role
import com.example.medical_schedule_app.data.models.requests.LoginRequest
import com.example.medical_schedule_app.data.models.requests.SignupBody
import com.example.medical_schedule_app.data.models.requests.SignupRequest
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
    suspend fun signup(request: SignupRequest, roleId: Int): Result<String> = withContext(Dispatchers.IO) {
        try {
            val body = SignupBody(
                name = request.name,
                password = request.password,
                specialty = request.specialty
            )



            // Call the appropriate signup method based on the roleId
            val response = when (roleId) {
                2 -> apiService.signupBranch(request.email, body) // Branch/Admin
                4 -> apiService.signupDoctor(request.email, body) // Doctor
                5 -> apiService.signupReceptionist(request.email, body) // Receptionist
                else -> throw IllegalArgumentException("Invalid role ID for signup")
            }

            // Check if the response is successful
            if (response.isSuccessful) {
                val token = response.body()?.token ?: return@withContext Result.failure(Exception("Empty token"))
                Result.success(token) // Return the token
            } else {
                Result.failure(Exception("Signup failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e) // Handle any other exceptions (e.g., network error)
        }
    }

}