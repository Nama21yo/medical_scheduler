package com.example.medical_schedule_app.data.models.responses

import com.example.medical_schedule_app.data.models.User
import com.google.gson.annotations.SerializedName

data class UserUpdateApiResponse(
    @SerializedName("message")
    val message: String,
    @SerializedName("updatedUser")
    val updatedUser: User
)