package com.example.medical_schedule_app.data.models.requests

data class AddPatientRequest(
    val first_name: String,
    val last_name: String,
    val date_of_birth: String, // Consider using a standard date format like "YYYY-MM-DD"
    val email: String?, // Email might be optional
    val address: String?, // Address might be optional
    val phone: String,
    val gender: String
)