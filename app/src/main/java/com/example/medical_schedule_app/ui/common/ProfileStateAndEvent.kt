package com.example.medical_schedule_app.ui.common

import com.example.medical_schedule_app.data.models.User

data class ProfileState(
    val user: User? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val editUsername: String = "",
    val editEmail: String = "",
    val editEnterPassword: String = "",
    val editNewPassword: String = "",
    val editConfirmPassword: String = "",
    val fetchError: String? = null,
    val updateInProgress: Boolean = false,
    val updateSuccess: Boolean = false,
    val updateError: String? = null
)

sealed class ProfileEvent {
    object FetchUserProfile : ProfileEvent()
    object AttemptUpdateProfile : ProfileEvent()
    object ToggleEditMode : ProfileEvent()
    object ClearMessages : ProfileEvent()
    data class OnUsernameChanged(val username: String) : ProfileEvent()
    data class OnEmailChanged(val email: String) : ProfileEvent()
    data class OnEnterPasswordChanged(val password: String) : ProfileEvent()
    data class OnNewPasswordChanged(val password: String) : ProfileEvent()
    data class OnConfirmPasswordChanged(val password: String) : ProfileEvent()
}
