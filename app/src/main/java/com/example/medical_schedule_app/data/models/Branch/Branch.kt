package com.example.medical_schedule_app.data.models.Branch

data class Branch(
    val branch_id: Int,
    val name: String,
    val location: String,
    val contact_email: String,
    val contact_phone: String,
    val password: String,
    val specialization: String?, // nullable
    val is_signed_up: Boolean,
    val created_at: String,
    val updated_at: String
)
