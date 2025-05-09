package com.example.medical_schedule_app.ui.auth

import com.example.medical_schedule_app.data.models.Role

sealed class AuthUiEvent {
    data class ToggleTab(val isLogin: Boolean) : AuthUiEvent()
    data class UpdateName(val value: String) : AuthUiEvent()
    data class UpdateEmail(val value: String) : AuthUiEvent()
    data class UpdatePassword(val value: String) : AuthUiEvent()
    data class UpdateConfirmPassword(val value: String) : AuthUiEvent()
    data class UpdateSpecialty(val value: String) : AuthUiEvent()
    data class SelectRole(val role: Role) : AuthUiEvent()
    object Submit : AuthUiEvent()
}
