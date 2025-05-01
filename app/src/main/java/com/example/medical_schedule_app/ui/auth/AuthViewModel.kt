package com.example.medical_schedule_app.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.models.Role
import com.example.medical_schedule_app.data.repositories.UserRepository
import com.example.medical_schedule_app.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _roles = MutableLiveData<List<Role>>()
    val roles: LiveData<List<Role>> = _roles

    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult

    private val _signupResult = MutableLiveData<Result<String>>()
    val signupResult: LiveData<Result<String>> = _signupResult

    private val _selectedRole = MutableLiveData<Role>()
    val selectedRole: LiveData<Role> = _selectedRole

    init {
        loadRoles()
    }

    fun loadRoles() {
        viewModelScope.launch {
            try {
                val rolesList = userRepository.getRoles()
                _roles.value = rolesList
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = userRepository.login(email, password)
            result.onSuccess { token ->
                sessionManager.saveAuthToken(token)
                // Save role if selected
                _selectedRole.value?.let { role ->
                    sessionManager.saveUserRole(role)
                }
            }
            _loginResult.value = result
        }
    }

    fun signup(email: String, roleId: Int) {
        viewModelScope.launch {
            val result = userRepository.signup(email, roleId)
            result.onSuccess { token ->
                sessionManager.saveAuthToken(token)
                // Save role if selected
                _selectedRole.value?.let { role ->
                    sessionManager.saveUserRole(role)
                }
            }
            _signupResult.value = result
        }
    }

    fun setSelectedRole(role: Role) {
        _selectedRole.value = role
    }
}