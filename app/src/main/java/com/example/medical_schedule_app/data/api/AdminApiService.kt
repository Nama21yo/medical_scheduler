package com.example.medical_schedule_app.data.api

import com.example.medical_schedule_app.data.models.requests.DoctorRequest
import com.example.medical_schedule_app.data.models.requests.ReceptionistRequest
import com.example.medical_schedule_app.data.models.responses.AdminDoctorResponse
import com.example.medical_schedule_app.data.models.responses.AdminReceptionistResponse
import com.example.medical_schedule_app.data.models.User

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.Response

interface AdminApiService {

    @POST("doctors")
    suspend fun addDoctor(@Body doctor: DoctorRequest): AdminDoctorResponse

    @POST("receptionists")
    suspend fun addReceptionist(@Body receptionist: ReceptionistRequest): AdminReceptionistResponse

    @DELETE("users/{id}")
    suspend fun deleteUser(@Path("id") userId: Int): Response<Unit>

    @GET("users")
    suspend fun getAllUsers(): List<User>

}
