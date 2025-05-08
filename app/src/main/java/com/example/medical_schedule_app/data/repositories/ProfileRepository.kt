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
            val updatedUser = apiService.updateUserProfile(token, userId, userUpdateRequest)
            emit(NetworkResult.Success(updatedUser))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Failed to update user profile"))
        }
    }.flowOn(Dispatchers.IO)
}
