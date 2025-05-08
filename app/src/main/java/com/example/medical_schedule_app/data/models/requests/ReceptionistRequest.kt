// ReceptionistRequest.kt
package com.example.medical_schedule_app.data.models.requests

import com.google.gson.annotations.SerializedName

data class ReceptionistRequest(
    val email: String,
    @SerializedName("branch_id") val branchId: Int
)
