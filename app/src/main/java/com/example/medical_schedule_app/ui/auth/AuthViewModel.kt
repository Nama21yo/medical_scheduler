package com.example.medical_schedule_app.ui.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.api.ApiService
import com.example.medical_schedule_app.data.models.requests.SignupRequest
import com.example.medical_schedule_app.data.repositories.UserRepository
import com.example.medical_schedule_app.utils.ActualSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiService: ApiService,
    private val sessionManager: ActualSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        loadRoles()
    }

    fun onEvent(event: AuthUiEvent) {
        when (event) {
            is AuthUiEvent.ToggleTab -> _uiState.update { it.copy(isLogin = event.isLogin) }
            is AuthUiEvent.UpdateName -> _uiState.update { it.copy(name = event.value) }
            is AuthUiEvent.UpdateEmail -> _uiState.update { it.copy(email = event.value) }
            is AuthUiEvent.UpdatePassword -> _uiState.update { it.copy(password = event.value) }
            is AuthUiEvent.UpdateConfirmPassword -> _uiState.update { it.copy(confirmPassword = event.value) }
            is AuthUiEvent.UpdateSpecialty -> _uiState.update { it.copy(specialty = event.value) }
            is AuthUiEvent.SelectRole -> _uiState.update { it.copy(selectedRole = event.role) }
            AuthUiEvent.Submit -> handleSubmit()
        }
    }

    private fun loadRoles() {
        viewModelScope.launch {
            try {
                val roles = userRepository.getRoles()
                _uiState.update { it.copy(roles = roles) }
            } catch (_: Exception) {}
        }
    }

    private fun handleSubmit() {
        val state = _uiState.value
        viewModelScope.launch {
            if (state.isLogin) {
                val result = userRepository.login(state.email, state.password)
                result.onSuccess {
                    sessionManager.saveAuthToken(it)
                    state.selectedRole?.role_id?.let(sessionManager::saveUserId)
                    _uiState.update { it.copy(loginSuccess = true) }
                }
            } else {
                if (state.password == state.confirmPassword) {
                    val req = SignupRequest(
                        name = state.name,
                        email = state.email,
                        password = state.password,
                        specialty = state.specialty
                    )
                    val result = userRepository.signup(req, state.selectedRole?.role_id ?: return@launch)
                    result.onSuccess {
                        _uiState.update { it.copy(loginSuccess = true) }
                    }
                }
            }
        }
    }

    fun logout() {
        Log.d("AuthViewModel", "Logout called. Clearing session and resetting states.")
        viewModelScope.launch {
            sessionManager.clearSession()
            // reset it to it's default value
            _uiState.value = AuthUiState()

            Log.d("AuthViewModel", "Session cleared. Token after clear: ${sessionManager.fetchAuthToken()}")

            // load the roles again
            loadRoles()

            // Mark logout success to trigger navigation in the UI
            _uiState.update { it.copy(logoutSuccess = true) }
        }
    }

}
