package com.example.medical_schedule_app.data.models.responses

data class DiagnosisResponse(
    val diagnosis_id: Int,
    val diagnosis_name: String,
    val diagnosis_details: String,
    val prescription: String,
    val visible: Boolean,
    val created_at: String,
    val updated_at: String,
    val patient: PatientResponse,
    val doctor: DoctorResponse
)