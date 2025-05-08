// DoctorRequest.kt
package com.example.medical_schedule_app.data.models.requests

import com.google.gson.annotations.SerializedName

data class DoctorRequest(
    val name: String,
    val email: String,
    @SerializedName("branch_id") val branchId: Int
)
