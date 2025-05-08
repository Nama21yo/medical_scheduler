package com.example.medical_schedule_app.ui.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.models.requests.DoctorRequest
import com.example.medical_schedule_app.data.models.requests.ReceptionistRequest
import com.example.medical_schedule_app.data.repositories.AdminRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEmployeeViewModel @Inject constructor(
    private val adminRepository: AdminRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AddEmployeeState())
    val state: StateFlow<AddEmployeeState> = _state.asStateFlow()

    fun onEvent(event: AddEmployeeEvent) {
        when (event) {
            is AddEmployeeEvent.OnNameChanged -> {
                _state.update { it.copy(name = event.name, error = null) }
            }
            is AddEmployeeEvent.OnEmailChanged -> {
                _state.update { it.copy(email = event.email, error = null) }
            }
            is AddEmployeeEvent.OnBranchIdChanged -> {
                _state.update { it.copy(branchId = event.branchId, error = null) }
            }
            is AddEmployeeEvent.OnRoleSelected -> {
                _state.update { it.copy(selectedRole = event.role, showRoleDropdown = false, error = null) }
            }
            is AddEmployeeEvent.OnAddEmployeeClicked -> {
                addEmployee()
            }
            is AddEmployeeEvent.ToggleRoleDropdown -> {
                _state.update { it.copy(showRoleDropdown = !it.showRoleDropdown) }
            }
            is AddEmployeeEvent.DismissRoleDropdown -> {
                _state.update { it.copy(showRoleDropdown = false) }
            }
            is AddEmployeeEvent.ResetSuccessState -> {
                _state.update { it.copy(addEmployeeSuccess = false) }
            }
        }
    }


    private fun addEmployee() {
        val currentState = _state.value
        if (currentState.email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(currentState.email).matches()) {
            _state.update { it.copy(error = "Please enter a valid email address.") }
            return
        }
        if (currentState.selectedRole.isBlank()) {
            _state.update { it.copy(error = "Please select a role.") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = if (currentState.selectedRole.equals("Doctor", ignoreCase = true)) {
                adminRepository.addDoctor(
                    DoctorRequest(
                        name = currentState.name,
                        email = currentState.email,
                        branchId = currentState.branchId
                    )
                )
            } else {
                adminRepository.addReceptionist(
                    ReceptionistRequest(
                        email = currentState.email,
                        branchId = currentState.branchId
                    )
                )
            }


            result.fold(
                onSuccess = {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            addEmployeeSuccess = true,
                            email = "", // Clear form on success
                            // selectedRole remains for convenience if adding multiple of same type
                        )
                    }
                },
                onFailure = { exception ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = exception.message ?: "Failed to add employee. Please try again."
                        )
                    }
                }
            )
        }
    }
}