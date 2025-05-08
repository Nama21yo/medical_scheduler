package com.example.medical_schedule_app.data.models.requests

data class AddPatientRequest(
    val firstName: String,
    val lastName: String,
    val dob: String, // Consider using a standard date format like "YYYY-MM-DD"
    val email: String?, // Email might be optional
    val address: String?, // Address might be optional
    val phone: String
)