package com.example.medical_schedule_app.data.models.requests

import com.google.gson.annotations.SerializedName

data class UserUpdateRequest(
//    @SerializedName("email")
    val email: String,
//    @SerializedName("username")
    val username: String,
//    @SerializedName("password")
    val password: String? // Nullable if password change is optional
)