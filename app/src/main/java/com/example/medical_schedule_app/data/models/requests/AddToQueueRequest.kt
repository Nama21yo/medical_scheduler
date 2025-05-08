package com.example.medical_schedule_app.data.models.requests

data class AddToQueueRequest(
    val patient_id: Int,
    val doctor_id: Int,
    val status: Int
)