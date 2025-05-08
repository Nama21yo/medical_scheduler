package com.example.medical_schedule_app.data.repositories

import com.example.medical_schedule_app.data.api.AdminApiService
import com.example.medical_schedule_app.data.models.User
import com.example.medical_schedule_app.data.models.requests.DoctorRequest
import com.example.medical_schedule_app.data.models.requests.ReceptionistRequest
import com.example.medical_schedule_app.data.models.responses.AdminDoctorResponse
import com.example.medical_schedule_app.data.models.responses.AdminReceptionistResponse
import com.example.medical_schedule_app.data.models.responses.DoctorResponse
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val api: AdminApiService
) {

    suspend fun addDoctor(request: DoctorRequest): Result<AdminDoctorResponse> {
        return try {
            val response = api.addDoctor(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun addReceptionist(request: ReceptionistRequest): Result<AdminReceptionistResponse> {
        return try {
            val response = api.addReceptionist(request)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    suspend fun deleteUserById(userId: Int): Result<Unit> {
        return try {
            api.deleteUser(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllUsers(): Result<List<User>> {
        return try {
            val response = api.getAllUsers()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
