package com.example.medical_schedule_app.ui.doctor

data class DiagnosisFormState(
    val isLoading: Boolean = false,
    val diagnosisName: String = "",
    val diagnosisDetails: String = "",
    val prescription: String = "",
    val patientId: Int = 0,
    val error: String? = null,
    val isSuccess: Boolean = false
)

sealed class DiagnosisFormEvent {
    data class OnDiagnosisNameChanged(val name: String) : DiagnosisFormEvent()
    data class OnDiagnosisDetailsChanged(val details: String) : DiagnosisFormEvent()
    data class OnPrescriptionChanged(val prescription: String) : DiagnosisFormEvent()
    data class SetPatientId(val patientId: Int) : DiagnosisFormEvent()
    object OnSubmitClicked : DiagnosisFormEvent()
}