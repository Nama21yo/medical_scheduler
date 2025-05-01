package com.example.medical_schedule_app.data.models.requests

data class DiagnosisRequest(
    val diagnosis_name: String,
    val diagnosis_details: String,
    val prescription: String,
    val visible: Boolean = true,
    val patient_id: Int
)