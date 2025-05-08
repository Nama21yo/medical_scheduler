package com.example.medical_schedule_app.data.api

import com.example.medical_schedule_app.data.models.requests.AddPatientRequest // Import new request
import com.example.medical_schedule_app.data.models.requests.DiagnosisRequest
import com.example.medical_schedule_app.data.models.requests.StatusUpdateRequest
import com.example.medical_schedule_app.data.models.responses.DiagnosisResponse
import com.example.medical_schedule_app.data.models.responses.PatientResponse // Import new response
import com.example.medical_schedule_app.data.models.responses.QueueResponse
import com.example.medical_schedule_app.data.models.requests.AddToQueueRequest
import retrofit2.http.*

interface ReceptionistApiService {
    @GET("queues")
    suspend fun getQueues(): List<QueueResponse>

    @PUT("queues/{id}")
    suspend fun updateQueueStatus(
        @Path("id") id: Int,
        @Body statusUpdateRequest: StatusUpdateRequest
    ): QueueResponse

    // --- New Endpoints for Patients ---

    /**
     * Adds a new patient to the system.
     * The patient details are sent in the request body.
     */
    @POST("patients") // Assuming your endpoint for adding patients is /patients
    suspend fun addPatient(
        @Body patientRequest: AddPatientRequest
    ): PatientResponse // Assuming the API returns the created patient details

    /**
     * Searches for existing patients based on a query.
     * The query    parameter can be used to search by name, phone, or other criteria
     * handled by the backend.
     * Alternatively, you could have more specific query parameters.
     */
    @GET("patients/search") // Example search endpoint, or use /patients with query params
    suspend fun searchPatients(
        @Query("query") searchQuery: String // A general search term
        // OR more specific parameters like:
        // @Query("name") name: String? = null,
        // @Query("phone") phone: String? = null
    ): List<PatientResponse> // Returns a list of matching patients

    /**
     * Alternative search endpoint directly on the /patients collection.
     * This is often preferred RESTfully.
     */
    @GET("patients")
    suspend fun findPatients(
        @Query("name") name: String? = null,
        @Query("phone") phone: String? = null,
        @Query("email") email: String? = null
        // Add other searchable fields as needed
    ): List<PatientResponse>

    @POST("queues")
    suspend fun addPatientToQueue(
        @Body addToQueueRequest: AddToQueueRequest
    )
}