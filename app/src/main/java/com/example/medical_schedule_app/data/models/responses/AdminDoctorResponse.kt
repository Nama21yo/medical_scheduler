package com.example.medical_schedule_app.data.models.responses

import com.example.medical_schedule_app.data.models.Branch.Branch

data class AdminDoctorResponse(
    val doctor_id: Int,
    val name: String?,
    val email: String,
    val branch: Branch,
    val specialty: String?,
    val password: String?,
    val is_signed_up: Boolean,
    val created_at: String,
    val updated_at: String
)
