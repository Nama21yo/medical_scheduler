package com.example.medical_schedule_app.data.models.responses

data class PatientResponse(
    val patient_id: Int,
    val first_name: String,
    val last_name: String,
    val email: String,
    val phone_number: String,
    val date_of_birth: String,
    val gender: String,
    val address: String,
    val created_at: String,
    val updated_at: String
)