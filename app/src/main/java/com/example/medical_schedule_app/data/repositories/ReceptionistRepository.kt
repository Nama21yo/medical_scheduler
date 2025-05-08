package com.example.medical_schedule_app.data.repositories

import com.example.medical_schedule_app.data.api.ReceptionistApiService
import com.example.medical_schedule_app.data.models.requests.AddPatientRequest
import com.example.medical_schedule_app.data.models.requests.AddToQueueRequest
import com.example.medical_schedule_app.data.models.requests.StatusUpdateRequest
import com.example.medical_schedule_app.data.models.responses.PatientResponse
import com.example.medical_schedule_app.data.models.responses.QueueResponse
import com.example.medical_schedule_app.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReceptionistRepository @Inject constructor(private val receptionistApiService: ReceptionistApiService) {

    suspend fun getQueues(): Flow<NetworkResult<List<QueueResponse>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = receptionistApiService.getQueues()
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun addPatient(patient: AddPatientRequest): Flow<NetworkResult<PatientResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = receptionistApiService.addPatient(patient)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun updateQueueStatus(queueId: Int, status: Int): Flow<NetworkResult<QueueResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val requestBody = StatusUpdateRequest(status)
            val response = receptionistApiService.updateQueueStatus(queueId, requestBody)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun findPatients(
        patient_id: String? = null,
        phone_number: String? = null,
        email: String? = null
    ): Flow<NetworkResult<List<PatientResponse>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = receptionistApiService.findPatients(patient_id, phone_number, email)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }
    suspend fun addPatientToQueue(patient_id: Int): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            val request = AddToQueueRequest(
                patient_id = patient_id,
                doctor_id = 1, // Use an appropriate doctor_id
                status = 1     // Initial status set to 1
            )
            receptionistApiService.addPatientToQueue(request)
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

}