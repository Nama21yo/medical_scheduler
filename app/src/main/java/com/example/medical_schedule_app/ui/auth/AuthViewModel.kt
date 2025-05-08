package com.example.medical_schedule_app.ui.auth

import android.util.Log // For logging
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.api.ApiService
import com.example.medical_schedule_app.data.models.Role
import com.example.medical_schedule_app.data.models.requests.SignupRequest
import com.example.medical_schedule_app.data.repositories.UserRepository
import com.example.medical_schedule_app.utils.ActualSessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val apiService: ApiService, // Add this line for apiService
    val sessionManager: ActualSessionManager // Make public or provide getter for token check
) : ViewModel() {

    private val _roles = MutableLiveData<List<Role>>()
    val roles: LiveData<List<Role>> = _roles

    // Use a single event channel or StateFlow for navigation events if possible,
    // but for now, we'll reset these LiveData.
    private val _loginResult = MutableLiveData<Result<String>?>() // Nullable to indicate "no event"
    val loginResult: LiveData<Result<String>?> = _loginResult

    private val _signupResult = MutableLiveData<Result<String>?>() // Nullable
    val signupResult: LiveData<Result<String>?> = _signupResult

    private val _selectedRole = MutableLiveData<Role?>() // Nullable, reset on logout
    val selectedRole: LiveData<Role?> = _selectedRole

    // Expose a simple way to check if logged in, if LoginSignupScreen doesn't directly use sessionManager
    val isLoggedIn: Boolean
        get() = sessionManager.fetchAuthToken() != null

    init {
        Log.d("AuthViewModel", "INIT: Token: ${sessionManager.fetchAuthToken()}")
        loadRoles()
    }

    fun loadRoles() {
        viewModelScope.launch {
            try {
                val rolesList = userRepository.getRoles()
                _roles.value = rolesList
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error loading roles", e)
                // Handle error, maybe post to an error LiveData
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            result.onSuccess { token ->
                sessionManager.saveAuthToken(token)
                // selectedRole should already be set by the UI before calling login
                _selectedRole.value?.role_id?.let { roleId ->
                    sessionManager.saveUserId(roleId) // Save role_id as user_id
                    Log.d("AuthViewModel", "Login success, token and userId saved.")
                } ?: run {
                    Log.e("AuthViewModel", "Login success, but selectedRole or role_id is null.")
                    // This is an issue, role should be selected before login attempt.
                    // Potentially clear token and post failure to _loginResult if role is mandatory.
                }
            }
            _loginResult.postValue(result) // Post the result to trigger UI update/navigation
        }
    }

    fun signup(
        email: String,
        password: String,
        name: String,
        roleId: Int,
        specialty: String
    ) {
        viewModelScope.launch {
            val result = try {
                val request = SignupRequest(
                    password = password,
                    specialty = specialty,
                    name = name, // Make sure your SignupRequest has `email`
                    email = email
                )
                val result = userRepository.signup(request, roleId)

                result.onSuccess { token ->
                    sessionManager.saveAuthToken(token)
                    sessionManager.saveUserId(roleId)
                }

                result
            } catch (e: Exception) {
                Result.failure(e)
            }

            _signupResult.postValue(result)
        }
    }





    fun setSelectedRole(role: Role) {
        _selectedRole.value = role
    }

    fun logout() {
        Log.d("AuthViewModel", "Logout called. Clearing session and resetting states.")
        viewModelScope.launch {
            sessionManager.clearSession()
            _selectedRole.postValue(null)
            _loginResult.postValue(null) // Reset to null to prevent re-triggering
            _signupResult.postValue(null) // Reset to null
            Log.d("AuthViewModel", "Session cleared. Token after clear: ${sessionManager.fetchAuthToken()}")
        }
    }

    // Call this from LoginSignupScreen after processing a result to prevent re-navigation on config change
    fun clearAuthResults() {
        _loginResult.postValue(null)
        _signupResult.postValue(null)
    }
}