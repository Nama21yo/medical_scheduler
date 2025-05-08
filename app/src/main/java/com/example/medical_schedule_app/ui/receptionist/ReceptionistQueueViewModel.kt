package com.example.medical_schedule_app.ui.receptionist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.data.repositories.ReceptionistRepository
import com.example.medical_schedule_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.text.lowercase

@HiltViewModel
class ReceptionistQueueViewModel @Inject constructor(
    private val receptionistRepository: ReceptionistRepository
) : ViewModel(){
    private val _state = MutableStateFlow(ReceptionistQueueState())
    val state: StateFlow<ReceptionistQueueState> = _state

    init {
        onEvent(ReceptionistQueueEvent.FetchQueues)
    }

    fun onEvent(event: ReceptionistQueueEvent){
        when(event){
            is ReceptionistQueueEvent.FetchQueues -> {
                fetchQueues()
            }
            is ReceptionistQueueEvent.UpdateQueueStatus -> {
                updateQueueStatus(event.queueId, event.status)
            }
            is ReceptionistQueueEvent.OnSearchQueueChange -> {
                _state.update { it.copy(searchQueue = event.searchQueue) }
                filterQueues() // Call the filtering function
            }
            is ReceptionistQueueEvent.OnSearchDataBaseSearchChange -> {
                _state.update { it.copy(searchDataBaseSearch = event.searchDataBaseSearch) }
                searchDatabase() // Call the database search function
            }
        }

    }

    private fun fetchQueues() {
        viewModelScope.launch {
            receptionistRepository.getQueues().collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        val queues = result.data ?: emptyList()
                        val activeEntries = queues.size
                        val pendingEntries = queues.count { it.status == 2 }

                        _state.update {
                            it.copy(
                                isLoading = false,
                                queues = queues, // Store the original list
                                displayedQueues = queues, // Initially display the full list
                                error = null,
                                activeEntries = activeEntries,
                                pendingEntries = pendingEntries,
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
            receptionistRepository.updateQueueStatus(queueId, status).collect { result ->
                when (result) {
                    is NetworkResult.Success -> {
                        fetchQueues() // Refetch queues after update to reflect changes
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

    private fun filterQueues() {
        val originalQueues = _state.value.queues // Get the original list
        val searchTerm = _state.value.searchQueue.lowercase()

        val filteredList = if (searchTerm.isBlank()) {
            originalQueues // If search term is blank, display the original list
        } else {
            originalQueues.filter { queue ->
                (queue.patient.first_name + " " + queue.patient.last_name).lowercase().contains(searchTerm) ||
                        queue.patient.patient_id.toString().contains(searchTerm)
            }
        }
        _state.update { it.copy(displayedQueues = filteredList) } // Update the displayed list
    }

    private fun searchDatabase() {
        val searchTerm = _state.value.searchDataBaseSearch.lowercase()

        // Only perform a database search if the search term is not blank
        if (searchTerm.isNotBlank()) {
            viewModelScope.launch {
                receptionistRepository.findPatients(
                    patient_id = if (searchTerm.all { it.isDigit() }) searchTerm else null,
                    phone_number = if (searchTerm.all { it.isDigit() }) searchTerm else null,
                    email = if (searchTerm.contains("@")) searchTerm else null
                )
                    .collect { result ->
                        when (result) {
                            is NetworkResult.Loading -> {
                                _state.update { it.copy(isDatabaseSearchLoading = true, databaseSearchError = null) }
                            }
                            is NetworkResult.Success -> {
                                _state.update { it.copy(
                                    patients = result.data ?: emptyList(),
                                    isDatabaseSearchLoading = false,
                                    databaseSearchError = null
                                ) }
                            }
                            is NetworkResult.Error -> {
                                _state.update { it.copy(
                                    isDatabaseSearchLoading = false,
                                    databaseSearchError = result.message
                                ) }
                            }
                        }
                    }
            }
        } else {
            // If search term is blank, clear the database search results and reset loading/error states
            _state.update { it.copy(
                patients = emptyList(),
                isDatabaseSearchLoading = false,
                databaseSearchError = null
            ) }
        }
    }
}