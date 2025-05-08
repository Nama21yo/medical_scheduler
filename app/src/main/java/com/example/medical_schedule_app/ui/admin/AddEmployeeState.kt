package com.example.medical_schedule_app.ui.admin

data class AddEmployeeState(
    val name: String = "",
    val email: String = "",
    val branchId: Int = 0,
    val selectedRole: String = "Doctor", // Default selection
    val roles: List<String> = listOf("Doctor", "Receptionist"),
    val isLoading: Boolean = false,
    val error: String? = null,
    val addEmployeeSuccess: Boolean = false,
    val showRoleDropdown: Boolean = false
)
