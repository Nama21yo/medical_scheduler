package com.example.medical_schedule_app.data.api

import com.example.medical_schedule_app.data.models.requests.DiagnosisRequest
import com.example.medical_schedule_app.data.models.requests.StatusUpdateRequest
import com.example.medical_schedule_app.data.models.responses.DiagnosisResponse
import com.example.medical_schedule_app.data.models.responses.QueueResponse
import retrofit2.http.*

interface DoctorApiService {
    @GET("queues")
    suspend fun getQueues(): List<QueueResponse>

    @GET("diagnoses")
    suspend fun getDiagnoses(): List<DiagnosisResponse>

    @GET("diagnoses/{id}")
    suspend fun getDiagnosisById(@Path("id") id: String): DiagnosisResponse

    @POST("diagnoses")
    suspend fun createDiagnosis(@Body diagnosis: DiagnosisRequest): DiagnosisResponse

    @PUT("queues/{id}")
    suspend fun updateQueueStatus(
        @Path("id") id: Int,
        @Body statusUpdateRequest: StatusUpdateRequest
    ): QueueResponse

}