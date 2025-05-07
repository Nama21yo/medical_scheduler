package com.example.medical_schedule_app.ui.receptionist

data class AddPatientFormState(
    val isLoading: Boolean = false,
    val fullName: String = "",
    val address: String = "",
    val email: String = "",
    val dob: String = "",
    val phoneNumber: String = "",
    val error: String? = null,
    val isSuccess: Boolean = false,
    val fullNameError: String? = null,
    val addressError: String? = null,
    val emailError: String? = null,
    val dobError: String? = null,
    val phoneNumberError: String? = null
)

sealed class AddPatientFormEvent {
    data class OnFullNameChanged(val fullName: String) : AddPatientFormEvent()
    data class OnAddressChange(val address: String) : AddPatientFormEvent()
    data class OnEmailChange(val email: String) : AddPatientFormEvent()
    data class OnDobChange(val dob: String) : AddPatientFormEvent()
    data class OnPhoneNumberChange(val phoneNumber: String) : AddPatientFormEvent()
    object OnSubmitClicked : AddPatientFormEvent()
}