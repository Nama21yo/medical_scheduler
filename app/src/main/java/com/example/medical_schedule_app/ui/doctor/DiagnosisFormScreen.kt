package com.example.medical_schedule_app.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
// It's generally good to have scroll for forms, but I'll keep it as is if you prefer.
// import androidx.compose.foundation.rememberScrollState
// import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Correct import for back icon
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.medical_schedule_app.ui.components.MedicalAppBar // Ensure this import is correct

@Composable
fun DiagnosisFormScreen(
    navController: NavController,
    patientId: Int? = null,
    viewModel: DiagnosisFormViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Set patient ID when the screen is first composed
    LaunchedEffect(patientId) {
        if (patientId != null && patientId > 0) {
            viewModel.onEvent(DiagnosisFormEvent.SetPatientId(patientId))
        }
    }

    // Navigate back after successful submission
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            // Use navigateUp to go back to the previous screen (patient details)
            navController.navigateUp()
            // Consider adding viewModel.onEvent(DiagnosisFormEvent.ResetSuccessState) here
            // if you want to prevent re-navigation on configuration change or re-composition.
        }
    }

    // MedicalAppBar as defined in your components file does not have a 'navigationIcon' parameter.
    // It uses a SideNavigationBar and an AppTopBar (which doesn't have a leading icon slot).
    // If you need a back button in the AppBar, you would need to modify the MedicalAppBar
    // component definition (specifically its internal AppTopBar) to include a leadingIcon slot.
    // For now, removing the unsupported navigationIcon parameter to fix the compilation errors.
    // Back navigation upon success is handled by the LaunchedEffect. System back button
    // or an in-content back button would be other ways to navigate back before submission.
    MedicalAppBar(
        navController = navController,
        screenTitle = "Add Diagnosis"
        // Removed: navigationIcon = { ... IconButton for back ... }
    ) { paddingValues -> // Content lambda providing PaddingValues
        // Your original root Column for the screen content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // Apply padding from MedicalAppBar
                .background(Color(0xFFF0F8FF))
            // If this Column's content can exceed screen height, you might want to add:
            // .verticalScroll(rememberScrollState())
        ) {
            // This Column was your original main content container.
            // It already had its own padding, which is fine.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Add Diagnosis",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Diagnosis Name Field
                Text(
                    text = "Diagnosis",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.diagnosisName,
                    onValueChange = { viewModel.onEvent(DiagnosisFormEvent.OnDiagnosisNameChanged(it)) },
                    placeholder = { Text("Diagnosis Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2962FF),
                        unfocusedBorderColor = Color(0xFF2962FF)
                    )
                )

                // Medication Field
                Text(
                    text = "Medication",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.prescription,
                    onValueChange = { viewModel.onEvent(DiagnosisFormEvent.OnPrescriptionChanged(it)) },
                    placeholder = { Text("Medication") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2962FF),
                        unfocusedBorderColor = Color(0xFF2962FF)
                    )
                )

                // Comments Field
                Text(
                    text = "Comments",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = state.diagnosisDetails,
                    onValueChange = { viewModel.onEvent(DiagnosisFormEvent.OnDiagnosisDetailsChanged(it)) },
                    placeholder = { Text("Comments") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .padding(bottom = 24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF2962FF),
                        unfocusedBorderColor = Color(0xFF2962FF)
                    )
                )

                // Error message if any
                if (state.error != null) {
                    Text(
                        text = state.error!!,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                // Add Diagnosis Button
                Button(
                    onClick = { viewModel.onEvent(DiagnosisFormEvent.OnSubmitClicked) },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1)),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Text("Add Diagnosis")
                    }
                }
            }
        }
    }
}

// Placeholder data classes and ViewModel for compilation and preview (if needed)
// data class DiagnosisFormState(
//    val patientId: Int? = null,
//    val diagnosisName: String = "",
//    val prescription: String = "",
//    val diagnosisDetails: String = "",
//    val isLoading: Boolean = false,
//    val isSuccess: Boolean = false,
//    val error: String? = null
// )
//
// sealed class DiagnosisFormEvent {
//    data class SetPatientId(val id: Int) : DiagnosisFormEvent()
//    data class OnDiagnosisNameChanged(val name: String) : DiagnosisFormEvent()
//    data class OnPrescriptionChanged(val prescription: String) : DiagnosisFormEvent()
//    data class OnDiagnosisDetailsChanged(val details: String) : DiagnosisFormEvent()
//    object OnSubmitClicked : DiagnosisFormEvent()
//    // object ResetSuccessState : DiagnosisFormEvent() // Consider adding this
// }
//
// class DiagnosisFormViewModel : ViewModel() {
//    private val _state = MutableStateFlow(DiagnosisFormState())
//    val state: StateFlow<DiagnosisFormState> = _state.asStateFlow()
//    fun onEvent(event: DiagnosisFormEvent) {
//        // ... your existing event handling
//    }
// }