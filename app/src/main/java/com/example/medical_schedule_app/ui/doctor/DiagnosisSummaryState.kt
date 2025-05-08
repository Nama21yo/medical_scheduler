package com.example.medical_schedule_app.ui.diagnosis

data class DiagnosisSummaryState(
    val isLoading: Boolean = false,
    val diagnosis: DiagnosisDetails? = null,
    val error: String? = null
)

