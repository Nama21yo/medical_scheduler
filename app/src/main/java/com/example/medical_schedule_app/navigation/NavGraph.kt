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
import com.example.medical_schedule_app.ui.profile.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {
    // Get ViewModel using hiltViewModel
    val authViewModel: AuthViewModel = hiltViewModel() // This authViewModel can be passed down
    // Collect UI state from authViewModel
    val authState = authViewModel.uiState.collectAsState().value

    // Observe logout success and navigate to login screen if it's true
    if (authState.logoutSuccess) {
        navController.navigate(NavigationRoutes.AUTH) {
            popUpTo(navController.graph.id) { inclusive = true }
            launchSingleTop = true
        }
    }
    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.AUTH
    ) {
        // Auth screen
        composable(NavigationRoutes.AUTH) {
            LoginSignupScreen(
                viewModel = authViewModel, // Pass the existing authViewModel
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
                        else -> {
                            navController.navigate(NavigationRoutes.AUTH) {
                                popUpTo(NavigationRoutes.AUTH) { inclusive = true }
                            }
                        }
                    }
                }
            )
        }

        // Receptionist home screen
        composable(NavigationRoutes.RECEPTIONIST_HOME) {
            val receptionistViewModel: ReceptionistQueueViewModel = hiltViewModel()
            val state = receptionistViewModel.state.collectAsState()

            ReceptionistQueueScreenPhone(
                navController = navController,
                state = state.value,
                onEvent = receptionistViewModel::onEvent,
                onNavigateToAddPatient = {
                    navController.navigate(NavigationRoutes.ADD_PATIENT)
                },
                // Pass the authViewModel available in NavGraph scope
                authViewModel = authViewModel
                // onLogout and onUserProfileClick are removed as MedicalAppBar handles them
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
            // DoctorQueueScreen needs authViewModel if it uses MedicalAppBar
            DoctorQueueScreen(
                navController = navController
                // authViewModel = authViewModel // Add if DoctorQueueScreen uses MedicalAppBar
            )
        }
        // Diagnosis summary screen
        composable(
            route = NavigationRoutes.DIAGNOSIS_SUMMARY,
            arguments = listOf(navArgument("diagnosisId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diagnosisId = backStackEntry.arguments?.getString("diagnosisId") ?: ""
            DiagnosisSummaryScreen(
                diagnosisId = diagnosisId,
                navController = navController
                // authViewModel = authViewModel // Add if DiagnosisSummaryScreen uses MedicalAppBar
            )
        }

        // Diagnosis details screen
        composable(
            route = NavigationRoutes.DIAGNOSIS_DETAILS,
            arguments = listOf(navArgument("diagnosisId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diagnosisId = backStackEntry.arguments?.getString("diagnosisId") ?: ""
            DiagnosisDetailsScreen(
                diagnosisId = diagnosisId,
                navController = navController
                // authViewModel = authViewModel // Add if DiagnosisDetailsScreen uses MedicalAppBar
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
                // authViewModel = authViewModel // Add if DiagnosisFormScreen uses MedicalAppBar
            )
        }

        // Admin home screen
        composable(NavigationRoutes.ADMIN_HOME) {
            // AdminScreen now takes authViewModel
            AdminScreen(
                navController = navController,
                authViewModel = authViewModel // Pass the authViewModel
            )
        }

        // Add Employee screen (for Admin)
        composable(NavigationRoutes.ADD_EMPLOYEE) {
            // AddEmployeeScreen, if it were to use MedicalAppBar, would also need authViewModel
            // But we refactored it to use its own Scaffold.
            AddEmployeeScreen(navController = navController)
        }

        composable(NavigationRoutes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    // Handle home navigation dynamically based on role
                    navController.navigate(NavigationRoutes.DOCTOR_QUEUE) {
                        popUpTo(NavigationRoutes.PROFILE) { inclusive = true }
                    }
                },
                onNavigateToAuth = {
                    authViewModel.logout() // Perform logout action via ViewModel
                    // Navigate to Auth screen after logout
                    navController.navigate(NavigationRoutes.AUTH) {
                        popUpTo(navController.graph.id) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}