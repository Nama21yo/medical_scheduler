package com.example.medical_schedule_app.data.models.responses

import com.example.medical_schedule_app.data.models.User

data class UserUpdateApiResponse(
    val message: String,
    val updatedUser: User
)