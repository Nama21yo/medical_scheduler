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
                    }
                }
            )
        }

        // Receptionist home screen
        composable(NavigationRoutes.RECEPTIONIST_HOME) {
            val viewModel: ReceptionistQueueViewModel = hiltViewModel()
            val state = viewModel.state.collectAsState() // Collect StateFlow as State

            ReceptionistQueueScreenPhone(
                navController = navController,
                state = state.value, // Use the collected state
                onEvent = viewModel::onEvent,
                onNavigateToAddPatient = {
                    navController.navigate(NavigationRoutes.ADD_PATIENT)
                },
                onLogout = {
                    // Temporarily ignore logout logic for testing
                    navController.navigate(NavigationRoutes.AUTH) {
                        popUpTo(NavigationRoutes.RECEPTIONIST_HOME) { inclusive = true }
                    }
                },
                onUserProfileClick = {
                    navController.navigate(NavigationRoutes.PROFILE)
                }
            )
        }

        // Add Patient screen
        composable(NavigationRoutes.ADD_PATIENT) {
            AddPatientFormScreen(
                navController = navController,
                onSuccess = {
                    navController.popBackStack() // Navigate back to ReceptionistQueueScreenPhone
                }
            )
        }

        // Doctor queue screen
        composable(NavigationRoutes.DOCTOR_QUEUE) {
            DoctorQueueScreen(navController = navController)
        }
        // Diagnosis summary screen (updated route)
        composable(
            route = NavigationRoutes.DIAGNOSIS_SUMMARY,
            arguments = listOf(navArgument("diagnosisId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diagnosisId = backStackEntry.arguments?.getString("diagnosisId") ?: ""
            DiagnosisSummaryScreen(
                diagnosisId = diagnosisId,
                navController = navController
            )
        }

        // Patient history screen
        composable(
            route = NavigationRoutes.DIAGNOSIS_DETAILS,
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

        // Add placeholder screens for other roles (stub implementations for now)
        composable(NavigationRoutes.ADMIN_HOME) {
            // Placeholder for admin home screen
        }

//        composable(NavigationRoutes.RECEPTIONIST_HOME) {
//            // Placeholder for receptionist home screen
//        }
    }
}

//object NavigationRoutes {
//    const val AUTH = "auth"
//    const val DOCTOR_QUEUE = "doctor_queue"
//    const val PATIENT_HISTORY = "patient_history"
//    const val DIAGNOSIS_FORM = "diagnosis_form"
//    const val ADMIN_HOME = "admin_home"
//    const val RECEPTIONIST_HOME = "receptionist_home"
//}