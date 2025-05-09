package com.example.medical_schedule_app.data.repositories

import com.example.medical_schedule_app.data.api.AdminApiService
import com.example.medical_schedule_app.data.models.User
import com.example.medical_schedule_app.data.models.requests.AddPatientRequest
import com.example.medical_schedule_app.data.models.requests.DoctorRequest
import com.example.medical_schedule_app.data.models.requests.ReceptionistRequest
import com.example.medical_schedule_app.data.models.responses.AdminDoctorResponse
import com.example.medical_schedule_app.data.models.responses.AdminReceptionistResponse
import com.example.medical_schedule_app.data.models.responses.DoctorResponse
import com.example.medical_schedule_app.data.models.responses.PatientResponse
import com.example.medical_schedule_app.utils.NetworkResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AdminRepository @Inject constructor(
    private val api: AdminApiService
) {

    suspend fun addDoctor(doctor: DoctorRequest): Flow<NetworkResult<AdminDoctorResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.addDoctor(doctor)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }
//    suspend fun addDoctor(request: DoctorRequest): Result<AdminDoctorResponse> {
//        return try {
//            val response = api.addDoctor(request)
//            Result.success(response)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }

//    suspend fun addPatient(patient: AddPatientRequest): Flow<NetworkResult<PatientResponse>> = flow {
//        emit(NetworkResult.Loading())
//        try {
//            val response = receptionistApiService.addPatient(patient)
//            emit(NetworkResult.Success(response))
//        } catch (e: Exception) {
//            emit(NetworkResult.Error(e.message ?: "Unknown error"))
//        }
//    }

    suspend fun addReceptionist(receptionist: ReceptionistRequest): Flow<NetworkResult<AdminReceptionistResponse>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.addReceptionist(receptionist)
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

    suspend fun deleteUserById(userId: Int): Flow<NetworkResult<Unit>> = flow {
        emit(NetworkResult.Loading())
        try {
            api.deleteUser(userId)
            emit(NetworkResult.Success(Unit))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }


//    suspend fun deleteUserById(userId: Int): Result<Unit> {
//        return try {
//            api.deleteUser(userId)
//            Result.success(Unit)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }


    suspend fun getAllUsers(): Flow<NetworkResult<List<User>>> = flow {
        emit(NetworkResult.Loading())
        try {
            val response = api.getAllUsers()
            emit(NetworkResult.Success(response))
        } catch (e: Exception) {
            emit(NetworkResult.Error(e.message ?: "Unknown error"))
        }
    }

//    suspend fun getAllUsers(): Result<List<User>> {
//        return try {
//            val response = api.getAllUsers()
//            Result.success(response)
//        } catch (e: Exception) {
//            Result.failure(e)
//        }
//    }
}
