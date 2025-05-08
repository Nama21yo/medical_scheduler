package com.example.medical_schedule_app.navigation

object NavigationRoutes {
    const val AUTH = "auth"
    const val ADMIN_HOME = "admin_home"
    const val DOCTOR_HOME = "doctor_home" // Note: Not used in NavGraph's onLoginSuccess
    const val DOCTOR_QUEUE = "doctor_queue"
    const val DIAGNOSIS_FORM = "diagnosis_form"
    const val DIAGNOSIS_DETAILS = "diagnosis_details/{diagnosisId}"
    const val DIAGNOSIS_SUMMARY = "diagnosis_summary/{diagnosisId}"
    const val RECEPTIONIST_HOME = "receptionist_home"
    const val PATIENT_HOME = "patient_home" // Note: Not used in NavGraph's onLoginSuccess
    const val PROFILE = "profile"
    const val ADD_PATIENT = "add_patient" // For Receptionist
    const val PATIENT_HISTORY = "patient_history" // Note: Not used in NavGraph

    // Add this route for the Admin to add an employee
    const val ADD_EMPLOYEE = "add_employee"
}