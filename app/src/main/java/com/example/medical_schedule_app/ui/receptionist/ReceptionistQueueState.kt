package com.example.medical_schedule_app.ui.receptionist

import com.example.medical_schedule_app.data.models.responses.QueueResponse
import com.example.medical_schedule_app.data.models.responses.PatientResponse // Import PatientResponse if you use it in your state

data class ReceptionistQueueState(
    val isLoading: Boolean = false,
    val queues: List<QueueResponse> = emptyList(),
    val displayedQueues: List<QueueResponse> = emptyList(),
    val patients: List<PatientResponse> = emptyList(),
    val error: String? = null,
    val activeEntries: Int = 0,
    val pendingEntries: Int = 0,
    val searchDataBaseSearch: String = "",
    val searchQueue: String = "",
    val isDatabaseSearchLoading: Boolean = false,
    val databaseSearchError: String? = null,
)

sealed class  ReceptionistQueueEvent {
    object FetchQueues : ReceptionistQueueEvent()
    data class UpdateQueueStatus(val queueId: Int, val status: Int) : ReceptionistQueueEvent()
    data class OnSearchQueueChange(val searchQueue: String) : ReceptionistQueueEvent()
    data class OnSearchDataBaseSearchChange(val searchDataBaseSearch: String) : ReceptionistQueueEvent()
    data class AddToQueue(val patient_id: Int) : ReceptionistQueueEvent() // Event for adding a patient to the queue

}