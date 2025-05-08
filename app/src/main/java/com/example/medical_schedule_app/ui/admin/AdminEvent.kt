package com.example.medical_schedule_app.ui.admin

sealed class AdminEvent {
    object LoadDashboardData : AdminEvent()
    data class OnDeleteUserClicked(val userId: Int) : AdminEvent()
    data class OnSearchTermChanged(val term: String) : AdminEvent()
}