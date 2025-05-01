
package com.example.medical_schedule_app.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.medical_schedule_app.ui.components.MedicalAppBar

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
            navController.navigateUp()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF))
    ) {
        MedicalAppBar(
            title = "Add Diagnosis",
            navController = navController,
            showBackButton = true
        )

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