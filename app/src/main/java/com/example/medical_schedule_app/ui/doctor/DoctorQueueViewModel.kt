
package com.example.medical_schedule_app.ui.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.repositories.DoctorRepository
import com.example.medical_schedule_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DoctorQueueViewModel @Inject constructor(
    private val doctorRepository: DoctorRepository
) : ViewModel() {

    private val _state = MutableStateFlow(DoctorQueueState())
    val state: StateFlow<DoctorQueueState> = _state

    init {
        onEvent(DoctorQueueEvent.FetchQueues)
    }

    fun onEvent(event: DoctorQueueEvent) {
        when (event) {
            is DoctorQueueEvent.FetchQueues -> {
                fetchQueues()
            }
            is DoctorQueueEvent.UpdateQueueStatus -> {
                updateQueueStatus(event.queueId, event.status)
            }
            is DoctorQueueEvent.NavigateToPatientHistory -> {
                // Navigation is handled by the UI layer
            }
        }
    }

    private fun fetchQueues() {
        viewModelScope.launch {
            doctorRepository.getQueues().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val queues = result.data
                        val totalCompleted = queues.count { it.status == 3 }
                        val pending = queues.count { it.status == 2 }
                        val resolvedPending = queues.count { it.status == 3 }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                queues = queues,
                                error = null,
                                totalCompleted = totalCompleted,
                                pending = pending,
                                resolvedPending = resolvedPending
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

    private fun updateQueueStatus(queueId: Int, status: Int) {
        viewModelScope.launch {
            doctorRepository.updateQueueStatus(queueId, status).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        // Refresh the queue list after successful update
                        fetchQueues()
                    }
                    is NetworkResult.Error -> {
                        _state.update { it.copy(error = result.message) }
                    }
                    is NetworkResult.Loading -> {
                        _state.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
}