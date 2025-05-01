package com.example.medical_schedule_app.data.models

data class User(
    val user_id: Int,
    val username: String,
    val email: String,
    val role: Role,
    val created_at: String,
    val updated_at: String
)