
package com.example.medical_schedule_app.ui.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.models.requests.DiagnosisRequest
import com.example.medical_schedule_app.data.repositories.DoctorRepository
import com.example.medical_schedule_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosisFormViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DiagnosisFormState())
    val state: StateFlow<DiagnosisFormState> = _state

    fun onEvent(event: DiagnosisFormEvent) {
        when (event) {
            is DiagnosisFormEvent.OnDiagnosisNameChanged -> {
                _state.update { it.copy(diagnosisName = event.name) }
            }
            is DiagnosisFormEvent.OnDiagnosisDetailsChanged -> {
                _state.update { it.copy(diagnosisDetails = event.details) }
            }
            is DiagnosisFormEvent.OnPrescriptionChanged -> {
                _state.update { it.copy(prescription = event.prescription) }
            }
            is DiagnosisFormEvent.SetPatientId -> {
                _state.update { it.copy(patientId = event.patientId) }
            }
            is DiagnosisFormEvent.OnSubmitClicked -> {
                submitDiagnosis()
            }
        }
    }

    private fun submitDiagnosis() {
        val currentState = _state.value

        if (currentState.diagnosisName.isBlank() || currentState.diagnosisDetails.isBlank() ||
            currentState.prescription.isBlank() || currentState.patientId <= 0) {
            _state.update { it.copy(error = "All fields are required") }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val diagnosisRequest = DiagnosisRequest(
                diagnosis_name = currentState.diagnosisName,
                diagnosis_details = currentState.diagnosisDetails,
                prescription = currentState.prescription,
                patient_id = currentState.patientId
            )

            doctorRepository.createDiagnosis(diagnosisRequest).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        _state.update {
                            it.copy(
                                isLoading = false,
                                error = null,
                                isSuccess = true,
                                diagnosisName = "",
                                diagnosisDetails = "",
                                prescription = ""
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