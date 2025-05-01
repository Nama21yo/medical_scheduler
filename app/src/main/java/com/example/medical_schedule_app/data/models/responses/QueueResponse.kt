package com.example.medical_schedule_app.data.models.responses

data class QueueResponse(
    val queue_id: Int,
    val status: Int,
    val created_at: String,
    val updated_at: String,
    val patient: PatientResponse,
    val doctor: DoctorResponse
)