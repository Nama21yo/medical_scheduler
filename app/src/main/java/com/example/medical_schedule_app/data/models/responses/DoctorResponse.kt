package com.example.medical_schedule_app.data.models.responses

data class DoctorResponse(
    val user_id: Int,
    val username: String,
    val email: String,
    val created_at: String,
    val updated_at: String
)