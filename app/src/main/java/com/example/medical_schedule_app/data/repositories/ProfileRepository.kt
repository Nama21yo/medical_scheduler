package com.example.medical_schedule_app.data.repositories

import com.example.medical_schedule_app.data.api.ApiService
import com.example.medical_schedule_app.data.models.User
import com.example.medical_schedule_app.data.models.requests.UserUpdateRequest
import com.example.medical_schedule_app.utils.NetworkResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ProfileRepository @Inject constructor(private val apiService: ApiService) {

    suspend fun getUserProfile(token: String): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading())
        try {
            val user = apiService.getUserProfile(token)
            emit(NetworkResult.Success(user))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Failed to fetch user profile"))
        }
    }.flowOn(Dispatchers.IO)

    suspend fun updateUserProfile(
        token: String,
        userId: Int,
        userUpdateRequest: UserUpdateRequest
    ): Flow<NetworkResult<User>> = flow {
        emit(NetworkResult.Loading())
        try {
            // Assuming apiService.updateUserProfile returns Response<UserUpdateApiResponse>
            val response = apiService.updateUserProfile(
                "Bearer $token",
                userId,
                userUpdateRequest
            )

            val responseBody = response.body() // Get the body once to avoid multiple calls

            if (response.isSuccessful && responseBody?.updatedUser != null) {
                // Successfully got the wrapper and the nested user
                // At this point, responseBody and responseBody.updatedUser are guaranteed non-null
                // by the condition.
                emit(NetworkResult.Success(responseBody.updatedUser))
            } else {
                // This block handles:
                // 1. Non-successful HTTP responses (e.g., 4xx, 5xx)
                // 2. Successful HTTP responses but where response.body() is null
                // 3. Successful HTTP responses where response.body() is not null, but updatedUser is null
                val errorMessage = if (!response.isSuccessful) {
                    response.errorBody()?.string() ?: "API Error: ${response.code()}" // Message from error body or just code
                } else {
                    // Successful response, but data not in expected format
                    responseBody?.message ?: "User data missing in server response."
                }
                emit(NetworkResult.Error(message = errorMessage))
            }
        } catch (e: Exception) {
            // Catches other exceptions like IOException (network issues),
            // SerializationException (JSON parsing issues if response is malformed), etc.
            emit(NetworkResult.Error(message = e.localizedMessage ?: "An unexpected error occurred"))
        }
    }.flowOn(Dispatchers.IO)
}
