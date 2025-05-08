package com.example.medical_schedule_app.ui.diagnosis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.repositories.DoctorRepository
import com.example.medical_schedule_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiagnosisSummaryViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DiagnosisSummaryState())
    val state: StateFlow<DiagnosisSummaryState> = _state

    fun fetchDiagnosisSummary(id: String) {
        viewModelScope.launch {
            doctorRepository.getDiagnosisById(id).collect { result ->
                when (result) {
                    is NetworkResult.Loading -> {
                        _state.value = DiagnosisSummaryState(isLoading = true)
                    }
                    is NetworkResult.Success -> {
                        val details = result.data?.toDiagnosisDetails()
                        _state.value = DiagnosisSummaryState(diagnosis = details)
                    }
                    is NetworkResult.Error -> {
                        _state.value = DiagnosisSummaryState(error = result.message)
                    }
                }
            }
        }
    }
}
