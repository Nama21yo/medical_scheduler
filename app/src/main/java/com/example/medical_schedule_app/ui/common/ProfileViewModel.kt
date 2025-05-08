package com.example.medical_schedule_app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.models.requests.UserUpdateRequest
import com.example.medical_schedule_app.data.repositories.ProfileRepository
import com.example.medical_schedule_app.utils.ActualSessionManager
import com.example.medical_schedule_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val sessionManager: ActualSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileState())
    val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    init {
        onEvent(ProfileEvent.FetchUserProfile)
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.FetchUserProfile -> fetchUserProfile()
            is ProfileEvent.AttemptUpdateProfile -> validateAndUpdateProfile()
            is ProfileEvent.ToggleEditMode -> toggleEditMode()
            is ProfileEvent.ClearMessages -> clearMessages()
            is ProfileEvent.OnUsernameChanged -> _uiState.update { it.copy(editUsername = event.username) }
            is ProfileEvent.OnEmailChanged -> _uiState.update { it.copy(editEmail = event.email) }
            is ProfileEvent.OnEnterPasswordChanged -> _uiState.update { it.copy(editEnterPassword = event.password) }
            is ProfileEvent.OnNewPasswordChanged -> _uiState.update { it.copy(editNewPassword = event.password) }
            is ProfileEvent.OnConfirmPasswordChanged -> _uiState.update { it.copy(editConfirmPassword = event.password) }
        }
    }

    private fun fetchUserProfile() {
        viewModelScope.launch {
            val token = sessionManager.fetchAuthToken()
            if (token == null) {
                _uiState.update { it.copy(isLoading = false, fetchError = "Authentication token not found.") }
                return@launch
            }

            profileRepository.getUserProfile("Bearer $token").onEach { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is NetworkResult.Loading -> currentState.copy(isLoading = true, fetchError = null)
                        is NetworkResult.Success -> {
                            sessionManager.saveUserId(result.data.user_id)
                            currentState.copy(
                                isLoading = false,
                                user = result.data,
                                editUsername = result.data.username,
                                editEmail = result.data.email
                            )
                        }
                        is NetworkResult.Error -> currentState.copy(isLoading = false, fetchError = result.message)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun validateAndUpdateProfile() {
        val currentState = _uiState.value

        if (currentState.editUsername.isBlank()) {
            _uiState.update { it.copy(updateError = "Username cannot be empty.") }
            return
        }

        if (currentState.editEmail.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.editEmail).matches()) {
            _uiState.update { it.copy(updateError = "Invalid email address.") }
            return
        }

        var passwordForApi: String? = null

        if (currentState.editNewPassword.isNotEmpty()) {
            if (currentState.editNewPassword != currentState.editConfirmPassword) {
                _uiState.update { it.copy(updateError = "New passwords do not match.") }
                return
            }
            passwordForApi = currentState.editNewPassword
        } else if (currentState.editEnterPassword.isNotEmpty()) {
            passwordForApi = currentState.editEnterPassword
        }

        val userId = sessionManager.fetchUserId()
        if (userId == null) {
            _uiState.update { it.copy(updateError = "User ID not found. Cannot update.") }
            return
        }

        val token = sessionManager.fetchAuthToken()
        if (token == null) {
            _uiState.update { it.copy(updateInProgress = false, updateError = "Authentication token not found.") }
            return
        }

        val request = UserUpdateRequest(
            email = currentState.editEmail,
            username = currentState.editUsername,
            password = passwordForApi
        )

        updateUserProfile(userId, request, "Bearer $token")
    }

    private fun updateUserProfile(userId: Int, request: UserUpdateRequest, token: String) {
        viewModelScope.launch {
            profileRepository.updateUserProfile(token, userId, request).onEach { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is NetworkResult.Loading -> currentState.copy(updateInProgress = true, updateError = null, updateSuccess = false)
                        is NetworkResult.Success -> currentState.copy(
                            updateInProgress = false,
                            user = result.data,
                            updateSuccess = true,
                            isEditing = false,
                            editUsername = result.data.username,
                            editEmail = result.data.email,
                            editEnterPassword = "",
                            editNewPassword = "",
                            editConfirmPassword = ""
                        )
                        is NetworkResult.Error -> currentState.copy(updateInProgress = false, updateError = result.message, updateSuccess = false)
                    }
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun toggleEditMode() {
        _uiState.update { currentState ->
            val newEditMode = !currentState.isEditing
            if (newEditMode && currentState.user != null) {
                currentState.copy(
                    isEditing = newEditMode,
                    editUsername = currentState.user.username,
                    editEmail = currentState.user.email,
                    editEnterPassword = "",
                    editNewPassword = "",
                    editConfirmPassword = "",
                    updateSuccess = false,
                    updateError = null,
                    fetchError = null
                )
            } else {
                currentState.copy(
                    isEditing = newEditMode,
                    updateSuccess = false,
                    updateError = null,
                    fetchError = null
                )
            }
        }
    }

    private fun clearMessages() {
        _uiState.update {
            it.copy(fetchError = null, updateSuccess = false, updateError = null)
        }
    }
}
