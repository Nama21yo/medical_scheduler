package com.example.medical_schedule_app.data.models.responses

import com.example.medical_schedule_app.data.models.Branch.Branch

data class AdminReceptionistResponse(
    val receptionist_id: Int,
    val name: String?,
    val email: String,
    val branch: Branch,
    val password: String?,
    val is_signed_up: Boolean,
    val created_at: String,
    val updated_at: String
)
