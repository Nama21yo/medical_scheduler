package com.example.medical_schedule_app.ui.admin

import com.example.medical_schedule_app.data.models.User

data class AdminState(
    val totalDoctors: Int = 0,
    val totalReceptionists: Int = 0,
    val allUsers: List<User> = emptyList(), // Raw list from API
    val displayedUsers: List<User> = emptyList(), // List after search filtering
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchTerm: String = ""
)