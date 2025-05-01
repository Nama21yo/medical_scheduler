package com.example.medical_schedule_app.ui.doctor

import com.example.medical_schedule_app.data.models.responses.DiagnosisResponse
import com.example.medical_schedule_app.data.models.responses.PatientResponse

data class PatientHistoryState(
    val isLoading: Boolean = false,
    val patient: PatientResponse? = null,
    val diagnoses: List<DiagnosisResponse> = emptyList(),
    val error: String? = null
)

sealed class PatientHistoryEvent {
    data class FetchPatientHistory(val patientId: Int) : PatientHistoryEvent()
    data class NavigateToDiagnosisDetails(val diagnosisId: Int) : PatientHistoryEvent()
    object NavigateToAddDiagnosis : PatientHistoryEvent()
}


