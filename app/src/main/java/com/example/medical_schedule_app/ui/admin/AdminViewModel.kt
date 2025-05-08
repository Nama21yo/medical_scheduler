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
            AdminEvent.LoadDashboardData -> loadData()
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
            adminRepository.getAllUsers().fold(
                onSuccess = { users ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            allUsers = users,
                            totalDoctors = users.count { user -> user.role.role_id == 4 },
                            totalReceptionists = users.count { user -> user.role.role_id == 5 }
                        )
                    }
                    filterUsers() // Apply initial filter
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to load data."
                        )
                    }
                }
            )
        }
    }


    private fun deleteUser(userId: Int) {
        viewModelScope.launch {
            // Consider adding a loading state specific to deletion if needed
            _state.update { it.copy(isLoading = true) } // General loading for simplicity
            adminRepository.deleteUserById(userId).fold(
                onSuccess = {
                    // Reload data to reflect deletion
                    loadData()
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false, // Reset general loading
                            error = exception.message ?: "Failed to delete user."
                        )
                    }
                }
            )
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