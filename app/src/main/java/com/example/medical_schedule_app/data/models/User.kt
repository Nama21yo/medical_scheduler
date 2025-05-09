package com.example.medical_schedule_app.data.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("user_id")
    val user_id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("role")
    val role: Role,
    @SerializedName("created_at")
    val created_at: String,
    @SerializedName("updated_at")
    val updated_at: String
)