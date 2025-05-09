package com.example.medical_schedule_app.data.models

import com.google.gson.annotations.SerializedName

data class Role(
    @SerializedName("role_id")
    val role_id: Int,
    @SerializedName("name")
    val name: String
)