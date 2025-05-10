package com.example.medical_schedule_app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.medical_schedule_app.ui.admin.AdminScreen
import com.example.medical_schedule_app.ui.admin.AddEmployeeScreen
import com.example.medical_schedule_app.ui.common.ProfileScreen

@Composable
fun NavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState = authViewModel.uiState.collectAsState().value

    LaunchedEffect(authState.logoutSuccess) {
        if (authState.logoutSuccess) {
            navController.navigate(NavigationRoutes.AUTH) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = NavigationRoutes.AUTH
    ) {
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
                        else -> navController.navigate(NavigationRoutes.AUTH) {
                            popUpTo(NavigationRoutes.AUTH) { inclusive = true }
                        }
                    }
                }
            )
        }

        composable(NavigationRoutes.RECEPTIONIST_HOME) {
            val receptionistViewModel: ReceptionistQueueViewModel = hiltViewModel()
            val state = receptionistViewModel.state.collectAsState()
            ReceptionistQueueScreenPhone(
                navController = navController,
                state = state.value,
                onEvent = receptionistViewModel::onEvent,
                onNavigateToAddPatient = { navController.navigate(NavigationRoutes.ADD_PATIENT) },
                authViewModel = authViewModel
            )
        }

        composable(NavigationRoutes.ADD_PATIENT) {
            AddPatientFormScreen(
                navController = navController,
                onSuccess = { navController.popBackStack() }
            )
        }

        composable(NavigationRoutes.DOCTOR_QUEUE) {
            DoctorQueueScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

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

        composable(
            route = NavigationRoutes.DIAGNOSIS_DETAILS,
            arguments = listOf(navArgument("diagnosisId") { type = NavType.StringType })
        ) { backStackEntry ->
            val diagnosisId = backStackEntry.arguments?.getString("diagnosisId") ?: ""
            DiagnosisDetailsScreen(
                diagnosisId = diagnosisId,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(
            route = "${NavigationRoutes.DIAGNOSIS_FORM}?patientId={patientId}",
            arguments = listOf(navArgument("patientId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            })
        ) { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId")?.toIntOrNull()
            DiagnosisFormScreen(
                navController = navController,
                patientId = patientId,
                authViewModel = authViewModel
            )
        }

        composable(NavigationRoutes.ADMIN_HOME) {
            AdminScreen(
                navController = navController,
                authViewModel = authViewModel
            )
        }

        composable(NavigationRoutes.ADD_EMPLOYEE) {
            AddEmployeeScreen(navController = navController)
        }

        composable(NavigationRoutes.PROFILE) {
            ProfileScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateHome = {
                    navController.navigate(NavigationRoutes.DOCTOR_QUEUE) {
                        popUpTo(NavigationRoutes.PROFILE) { inclusive = true }
                    }
                },
                onNavigateToAuth = { authViewModel.logout() },
                authViewModel = authViewModel
            )
        }
    }
}