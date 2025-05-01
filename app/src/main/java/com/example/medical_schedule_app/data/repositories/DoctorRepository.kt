package com.example.medical_schedule_app.data.repositories

import com.example.medical_schedule_app.data.api.DoctorApiService
import com.example.medical_schedule_app.data.models.requests.DiagnosisRequest
import com.example.medical_schedule_app.data.models.requests.StatusUpdateRequest
import com.example.medical_schedule_app.data.models.responses.DiagnosisResponse
import com.example.medical_schedule_app.data.models.responses.QueueResponse
import com.example.medical_schedule_app.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DoctorRepository @Inject constructor(private val doctorApiService: DoctorApiService) {

    suspend fun getQueues(): Flow<NetworkResult<List<QueueResponse>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = doctorApiService.getQueues()
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun getDiagnoses(): Flow<NetworkResult<List<DiagnosisResponse>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = doctorApiService.getDiagnoses()
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun getDiagnosisById(id: String): Flow<NetworkResult<DiagnosisResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = doctorApiService.getDiagnosisById(id)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun createDiagnosis(diagnosis: DiagnosisRequest): Flow<NetworkResult<DiagnosisResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = doctorApiService.createDiagnosis(diagnosis)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun updateQueueStatus(queueId: Int, status: Int): Flow<NetworkResult<QueueResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val requestBody = StatusUpdateRequest(status)
            val response = doctorApiService.updateQueueStatus(queueId, requestBody)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

}