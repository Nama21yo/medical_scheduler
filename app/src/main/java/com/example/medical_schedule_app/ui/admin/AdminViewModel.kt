package com.example.medical_schedule_app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.repositories.AdminRepository
import com.example.medical_schedule_app.data.models.User // Make sure this import is correct
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.medical_schedule_app.utils.NetworkResult

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AdminState())
    val state: StateFlow<AdminState> = _state.asStateFlow()

    init {
        onEvent(AdminEvent.LoadDashboardData)
    }

    fun onEvent(event: AdminEvent) {
        when (event) {
            is AdminEvent.LoadDashboardData -> loadData()
            is AdminEvent.OnDeleteUserClicked -> deleteUser(event.userId)
            is AdminEvent.OnSearchTermChanged -> {
                _state.update { it.copy(searchTerm = event.term) }
                filterUsers()
            }
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            adminRepository.getAllUsers().collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        val users = result.data
                        _state.update {
                            it.copy(
                                isLoading = false,
                                allUsers = users,
                                totalDoctors = users.count { user -> user.role.role_id == 4 },
                                totalReceptionists = users.count { user -> user.role.role_id == 5 }
                            )
                        }
                        filterUsers() // Apply initial filter
                    }
                    is NetworkResult.Error -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
            }
        }
    }


    private fun deleteUser(userId: Int) {
        viewModelScope.launch {
            adminRepository.deleteUserById(userId).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                    is NetworkResult.Success -> {
                        loadData() // Reload data after successful deletion
                    }
                    is NetworkResult.Error -> {
                        _state.update {
                            it.copy(isLoading = false, error = result.message ?: "Failed to delete user.")
                        }
                    }
                }
            }
        }
    }

    private fun filterUsers() {
        val currentState = _state.value
        val term = currentState.searchTerm.trim()

        val filtered = if (term.isBlank()) {
            currentState.allUsers
        } else {
            currentState.allUsers.filter { user ->
                user.username.contains(term, ignoreCase = true) ||
                        user.email.contains(term, ignoreCase = true) ||
                        user.role.name.contains(term, ignoreCase = true) || // if searching by role name
                        term == "4" && user.role.role_id == 4 || // Doctor role ID
                        term == "5" && user.role.role_id == 5    // Receptionist role ID
            }
        }

        _state.update { it.copy(displayedUsers = filtered) }
    }

}