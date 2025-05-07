package com.example.medical_schedule_app.ui.receptionist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.models.requests.AddPatientRequest
import com.example.medical_schedule_app.data.repositories.ReceptionistRepository
import com.example.medical_schedule_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddPatientFormViewModel @Inject constructor(
    private val receptionistRepository: ReceptionistRepository
) : ViewModel() {
    private val _state = MutableStateFlow(AddPatientFormState())
    val state: StateFlow<AddPatientFormState> = _state

    fun onEvent(event: AddPatientFormEvent) {
        when (event) {
            is AddPatientFormEvent.OnFullNameChanged -> {
                _state.update { it.copy(fullName = event.fullName, fullNameError = null) }
            }
            is AddPatientFormEvent.OnAddressChange -> {
                _state.update { it.copy(address = event.address, addressError = null) }
            }
            is AddPatientFormEvent.OnEmailChange -> {
                _state.update { it.copy(email = event.email, emailError = null) }
            }
            is AddPatientFormEvent.OnDobChange -> {
                _state.update { it.copy(dob = event.dob, dobError = null) }
            }
            is AddPatientFormEvent.OnPhoneNumberChange -> {
                _state.update { it.copy(phoneNumber = event.phoneNumber, phoneNumberError = null) }
            }
            is AddPatientFormEvent.OnSubmitClicked -> {
                submitPatient()
            }
        }
    }

    private fun validateForm(): Boolean {
        val current = state.value
        var isValid = true
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$".toRegex()
        val phoneRegex = "^\\+?\\d{10,15}$".toRegex() // Allows + and 10-15 digits

        // Validate Full Name
        if (current.fullName.isBlank() || current.fullName.length < 2) {
            _state.update { it.copy(fullNameError = "Full name must be at least 2 characters") }
            isValid = false
        }

        // Validate DOB
        if (current.dob.isBlank()) {
            _state.update { it.copy(dobError = "Date of birth is required") }
            isValid = false
        }

        // Validate Email
        if (current.email.isBlank() || !emailRegex.matches(current.email)) {
            _state.update { it.copy(emailError = "Invalid email address") }
            isValid = false
        }

        // Validate Address
        if (current.address.isBlank()) {
            _state.update { it.copy(addressError = "Address is required") }
            isValid = false
        }

        // Validate Phone Number
        if (current.phoneNumber.isBlank() || !phoneRegex.matches(current.phoneNumber)) {
            _state.update { it.copy(phoneNumberError = "Invalid phone number (e.g., +1234567890)") }
            isValid = false
        }

        return isValid
    }

    private fun submitPatient() {
        if (!validateForm()) {
            _state.update { it.copy(error = "Please correct the errors above") }
            return
        }

        val current = state.value
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val patientRequest = AddPatientRequest(
                fullName = current.fullName,
                address = current.address,
                email = current.email,
                dob = current.dob,
                phone = current.phoneNumber
            )
            receptionistRepository.addPatient(patientRequest).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                isSuccess = true,
                                fullName = "",
                                address = "",
                                dob = "",
                                email = "",
                                phoneNumber = "",
                                fullNameError = null,
                                dobError = null,
                                emailError = null,
                                addressError = null,
                                phoneNumberError = null
                            )
                        }
                    }
                    is NetworkResult.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is NetworkResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
}