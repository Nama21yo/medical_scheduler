
package com.example.medical_schedule_app.ui.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.models.responses.PatientResponse
import com.example.medical_schedule_app.data.repositories.DoctorRepository
import com.example.medical_schedule_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientHistoryViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(PatientHistoryState())
    val state: StateFlow<PatientHistoryState> = _state

    fun onEvent(event: PatientHistoryEvent) {
        when (event) {
            is PatientHistoryEvent.FetchPatientHistory -> {
                fetchPatientHistory(event.patientId)
            }
            is PatientHistoryEvent.NavigateToDiagnosisDetails -> {
                // Navigation is handled by the UI layer
            }
            is PatientHistoryEvent.NavigateToAddDiagnosis -> {
                // Navigation is handled by the UI layer
            }
        }
    }

    private fun fetchPatientHistory(patientId: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            doctorRepository.getDiagnoses().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val diagnoses = result.data.filter { it.patient.patient_id == patientId }
                        val patient = if (diagnoses.isNotEmpty()) diagnoses.first().patient else null

                        _state.update {
                            it.copy(
                                isLoading = false,
                                diagnoses = diagnoses,
                                patient = patient,
                                error = null
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