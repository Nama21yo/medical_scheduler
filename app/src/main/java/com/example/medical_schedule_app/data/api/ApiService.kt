package com.example.medical_schedule_app.data.api

import com.example.medical_schedule_app.data.models.requests.LoginRequest
import com.example.medical_schedule_app.data.models.responses.LoginResponse
import com.example.medical_schedule_app.data.models.responses.RolesResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @GET("roles")
    suspend fun getRoles(): RolesResponse

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("users/branches/signup/{email}")
    suspend fun signupBranch(@Path("email") email: String): LoginResponse

    @POST("users/doctors/signup/{email}")
    suspend fun signupDoctor(@Path("email") email: String): LoginResponse

    @POST("users/receptionists/signup/{email}")
    suspend fun signupReceptionist(@Path("email") email: String): LoginResponse
}