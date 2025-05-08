package com.example.medical_schedule_app.data.api

import com.example.medical_schedule_app.data.models.User
import com.example.medical_schedule_app.data.models.requests.LoginRequest
import com.example.medical_schedule_app.data.models.requests.SignupBody
import com.example.medical_schedule_app.data.models.requests.UserUpdateRequest
import com.example.medical_schedule_app.data.models.responses.LoginResponse
import com.example.medical_schedule_app.data.models.responses.RolesResponse
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @GET("roles")
    suspend fun getRoles(): RolesResponse

    @POST("users/login")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse

    @POST("users/doctors/signup/{email}")
    suspend fun signupDoctor(
        @Path("email") email: String,
        @Body request: SignupBody
    ): Response<LoginResponse>

    @POST("users/receptionists/signup/{email}")
    suspend fun signupReceptionist(
        @Path("email") email: String,
        @Body request: SignupBody
    ): Response<LoginResponse>

    @POST("users/branches/signup/{email}")
    suspend fun signupBranch(
        @Path("email") email: String,
        @Body request: SignupBody
    ): Response<LoginResponse>


    @GET("users/user")
    suspend fun getUserProfile(@Header("Authorization") token: String): User

    @PUT("users/update/{userId}")
    suspend fun updateUserProfile(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int,
        @Body userUpdateRequest: UserUpdateRequest
    ): User
}
