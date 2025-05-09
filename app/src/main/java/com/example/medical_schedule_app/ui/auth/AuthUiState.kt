package com.example.medical_schedule_app.ui.auth

import com.example.medical_schedule_app.data.models.Role

data class AuthUiState(
    val isLogin: Boolean = true,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val specialty: String = "",
    val selectedRole: Role? = null,
    val roles: List<Role> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false,
    val logoutSuccess: Boolean = false
)
