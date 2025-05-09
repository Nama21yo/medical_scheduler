package com.example.medical_schedule_app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
//import com.example.medical_schedule_app.data.models.User // Ensure this is the correct User model
import com.example.medical_schedule_app.data.models.requests.UserUpdateRequest
import com.example.medical_schedule_app.data.models.responses.UserUpdateApiResponse
// You might need to define this or ensure your repository returns the nested User object directly
// import com.example.medical_schedule_app.data.models.responses.UserUpdateApiResponse
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

            // Assuming profileRepository.getUserProfile correctly returns NetworkResult<User>
            profileRepository.getUserProfile("Bearer $token").onEach { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is NetworkResult.Loading -> currentState.copy(isLoading = true, fetchError = null)
                        is NetworkResult.Success -> {
                            sessionManager.saveUserId(result.data.user_id) // result.data here is User
                            currentState.copy(
                                isLoading = false,
                                user = result.data,
                                editUsername = result.data.username,
                                editEmail = result.data.email,
                                fetchError = null // Clear previous fetch error
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
            // Only send new password if current password is also entered, or if admin doesn't require it
            // For simplicity now, we assume if new password is set, it's intended to be changed.
            // A robust solution would also check currentState.editEnterPassword if it's mandatory for password changes.
            passwordForApi = currentState.editNewPassword
        }
        // Removed the 'else if' for editEnterPassword for now as it's unclear if it's meant to update the password.
        // If updating profile without changing password, current password might be needed by backend for verification.
        // The UserUpdateRequest only has one 'password' field, which implies new password.

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
            password = passwordForApi // This will be null if not changing password
        )

        updateUserProfile(userId, request, "Bearer $token")
    }

    private fun updateUserProfile(userId: Int, request: UserUpdateRequest, token: String) {
        viewModelScope.launch {
            // CRITICAL: Adjust the type parameter of NetworkResult if your repository
            // returns the wrapper object (e.g., NetworkResult<UserUpdateApiResponse>)
            // If it already extracts 'updatedUser', then NetworkResult<User> is correct.
            // For this example, I'll assume the repository returns NetworkResult<UserUpdateApiResponse>
            // and we need to extract 'updatedUser'. If your repo already returns NetworkResult<User>
            // (where 'User' is the *updated* user), then the previous `result.data.username` would have been okay.

            profileRepository.updateUserProfile(token, userId, request).onEach { result ->
                _uiState.update { currentState ->
                    when (result) {
                        is NetworkResult.Loading -> currentState.copy(
                            updateInProgress = true,
                            updateError = null,
                            updateSuccess = false
                        )
                        is NetworkResult.Success -> {
                            // Assuming result.data is of type UserUpdateApiResponse
                            // or your repository already extracts the 'updatedUser' part.
                            // If result.data IS UserUpdateApiResponse:
                            val actualUpdatedUser = (result.data as? UserUpdateApiResponse)?.updatedUser

                            // Debug: Why this is Null when it is Printed?
                            println("The actual user data display: ${result.data}")
                            println("The formated user data display: $actualUpdatedUser")
                            if (actualUpdatedUser != null) {
                                // Also update the user ID in session manager if it could change (unlikely for update)
                                // sessionManager.saveUserId(actualUpdatedUser.user_id)
                                currentState.copy(
                                    updateInProgress = false,
                                    user = actualUpdatedUser, // Use the nested user object
                                    updateSuccess = true,
                                    updateError = null, // Clear previous update error
                                    isEditing = false,  // Exit edit mode
                                    editUsername = actualUpdatedUser.username, // Use properties from actualUpdatedUser
                                    editEmail = actualUpdatedUser.email,     // Use properties from actualUpdatedUser
                                    editEnterPassword = "", // Clear password fields
                                    editNewPassword = "",
                                    editConfirmPassword = ""
                                )
                            } else {
                                // Handle case where result.data is not in the expected format
                                currentState.copy(
                                    updateInProgress = false,
                                    updateError = "Invalid response format from server.",
                                    updateSuccess = false
                                )
                            }
                        }
                        is NetworkResult.Error -> currentState.copy(
                            updateInProgress = false,
                            updateError = result.message,
                            updateSuccess = false
                        )
                    }
                }
            }.launchIn(viewModelScope)
        }
    }


    private fun toggleEditMode() {
        _uiState.update { currentState ->
            val newEditMode = !currentState.isEditing
            if (newEditMode && currentState.user != null) {
                // When entering edit mode, populate fields from the current user state
                currentState.copy(
                    isEditing = newEditMode,
                    editUsername = currentState.user.username,
                    editEmail = currentState.user.email,
                    editEnterPassword = "", // Clear password fields when entering edit mode
                    editNewPassword = "",
                    editConfirmPassword = "",
                    updateSuccess = false, // Reset flags
                    updateError = null,
                    fetchError = null      // Reset fetch error as well
                )
            } else {
                // When exiting edit mode (e.g., by clicking "Profile" tab or cancelling)
                // Optionally, you might want to revert editUsername/editEmail to user.username/user.email
                // if changes were not saved. For now, just toggles edit mode and clears flags.
                currentState.copy(
                    isEditing = newEditMode,
                    updateSuccess = false,
                    updateError = null
                    // fetchError = null // Not necessarily cleared here unless intended
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