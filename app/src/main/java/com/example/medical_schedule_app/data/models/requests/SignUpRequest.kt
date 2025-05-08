package com.example.medical_schedule_app.data.models.requests

data class SignupRequest(
    val email: String,
    val name: String,
    val password: String,
    val specialty: String? = null
)

data class SignupBody(
    val name: String,
    val password: String,
    val specialty: String? = null
)
