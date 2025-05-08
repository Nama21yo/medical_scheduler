package com.example.medical_schedule_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.medical_schedule_app.ui.auth.AuthViewModel
import com.example.medical_schedule_app.ui.receptionist.ReceptionistQueueViewModel
import com.example.medical_schedule_app.ui.receptionist.AddPatientFormScreen
import com.example.medical_schedule_app.ui.auth.LoginSignupScreen
import com.example.medical_schedule_app.ui.diagnosis.DiagnosisSummaryScreen
import com.example.medical_schedule_app.ui.doctor.DiagnosisFormScreen
import com.example.medical_schedule_app.ui.doctor.DoctorQueueScreen
import com.example.medical_schedule_app.ui.doctor.DiagnosisDetailsScreen
import com.example.medical_schedule_app.ui.receptionist.ReceptionistQueueScreenPhone

// Import Admin screens
import com.example.medical_schedule_app.ui.admin.AdminScreen
import com.example.medical_schedule_app.ui.admin.AddEmployeeScreen

@Composable
fun NavGraph(navController: NavHostController) {
    // Get ViewModel using hiltViewModel
    val authViewModel: AuthViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.AUTH
    ) {
        // Auth screen
        composable(NavigationRoutes.AUTH) {
            LoginSignupScreen(
                viewModel = authViewModel,
                onLoginSuccess = { roleId ->
                    when (roleId) {
                        2 -> navController.navigate(NavigationRoutes.ADMIN_HOME) {
                            popUpTo(NavigationRoutes.AUTH) { inclusive = true }
                        }
                        4 -> navController.navigate(NavigationRoutes.DOCTOR_QUEUE) {
                            popUpTo(NavigationRoutes.AUTH) { inclusive = true }
                        }
                        5 -> navController.navigate(NavigationRoutes.RECEPTIONIST_HOME) {
                            popUpTo(NavigationRoutes.AUTH) { inclusive = true }
                        }
                        // Consider adding an 'else' case for unhandled roles
                    }
                }
            )
        }

        // Receptionist home screen
        composable(NavigationRoutes.RECEPTIONIST_HOME) {
            val viewModel: ReceptionistQueueViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState()

            ReceptionistQueueScreenPhone(
                navController = navController,
                state = state.value,
                onEvent = viewModel::onEvent,
                onNavigateToAddPatient = {
                    navController.navigate(NavigationRoutes.ADD_PATIENT)
                },
                onLogout = {
                    navController.navigate(NavigationRoutes.AUTH) {
                        popUpTo(NavigationRoutes.RECEPTIONIST_HOME) { inclusive = true }
                    }
                },
                onUserProfileClick = {
                    navController.navigate(NavigationRoutes.PROFILE)
                }
            )
        }

        // Add Patient screen (for Receptionist)
        composable(NavigationRoutes.ADD_PATIENT) {
            AddPatientFormScreen(
                navController = navController,
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        // Doctor queue screen
        composable(NavigationRoutes.DOCTOR_QUEUE) {
            DoctorQueueScreen(navController = navController)
        }
        // Diagnosis summary screen
        composable(
            route = NavigationRoutes.DIAGNOSIS_SUMMARY, // Note: DIAGNOSIS_SUMMARY is "diagnosis_summary/{diagnosisId}"
            arguments = listOf(navArgument("diagnosisId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diagnosisId = backStackEntry.arguments?.getString("diagnosisId") ?: ""
            DiagnosisSummaryScreen(
                diagnosisId = diagnosisId,
                navController = navController
            )
        }

        // Diagnosis details screen (renamed from Patient History in your previous NavGraph)
        composable(
            route = NavigationRoutes.DIAGNOSIS_DETAILS, // Note: DIAGNOSIS_DETAILS is "diagnosis_details/{diagnosisId}"
            arguments = listOf(navArgument("diagnosisId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diagnosisId = backStackEntry.arguments?.getString("diagnosisId") ?: ""
            DiagnosisDetailsScreen(
                diagnosisId = diagnosisId,
                navController = navController
            )
        }

        // Diagnosis form screen
        composable(
            route = "${NavigationRoutes.DIAGNOSIS_FORM}?patientId={patientId}",
            arguments = listOf(navArgument("patientId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")
            DiagnosisFormScreen(
                navController = navController,
                patientId = patientId?.toIntOrNull()
            )
        }

        // Admin home screen
        composable(NavigationRoutes.ADMIN_HOME) {
            AdminScreen(navController = navController) // Use the actual AdminScreen
        }

        // Add Employee screen (for Admin)
        composable(NavigationRoutes.ADD_EMPLOYEE) {
            AddEmployeeScreen(navController = navController) // Use the actual AddEmployeeScreen
        }

        // composable(NavigationRoutes.PROFILE) { ... } // Placeholder if you have a profile screen
    }
}