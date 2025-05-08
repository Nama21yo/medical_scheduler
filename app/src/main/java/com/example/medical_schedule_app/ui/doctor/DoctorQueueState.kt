    package com.example.medical_schedule_app.ui.doctor

    import com.example.medical_schedule_app.data.models.responses.QueueResponse

    data class DoctorQueueState(
        val isLoading: Boolean = false,
        val queues: List<QueueResponse> = emptyList(),
        val error: String? = null,
        val totalCompleted: Int = 0,
        val pending: Int = 0,
        val resolvedPending: Int = 0
    )

    sealed class DoctorQueueEvent {
        object FetchQueues : DoctorQueueEvent()
        data class UpdateQueueStatus(val queueId: Int, val status: Int) : DoctorQueueEvent()
        data class NavigateToPatientHistory(val patientId: Int) : DoctorQueueEvent()
    }