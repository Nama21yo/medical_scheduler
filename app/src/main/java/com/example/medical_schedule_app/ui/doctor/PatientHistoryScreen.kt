package com.example.medical_schedule_app.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medical_schedule_app.navigation.NavigationRoutes
import com.example.medical_schedule_app.ui.components.MedicalAppBar
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DiagnosisDetailsScreen(
    diagnosisId: String,
    navController: NavController,
    viewModel: PatientHistoryViewModel = hiltViewModel()
) {
    val patientId = diagnosisId.toIntOrNull() ?: 0
    val state by viewModel.state.collectAsState()
    val blueColor = Color(0xFF3D6FB4)

    LaunchedEffect(patientId) {
        if (patientId > 0) {
            viewModel.onEvent(PatientHistoryEvent.FetchPatientHistory(patientId))
        }
    }

    MedicalAppBar(
        navController = navController,
        screenTitle = "Patient History"
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF0F8FF))
                .padding(paddingValues)
        ) {
            if (state.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${state.error}",
                        color = Color.Red
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    // Patient Info Card
                    state.patient?.let { patient ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = blueColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Name: ${patient.first_name} ${patient.last_name}",
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Age: ${calculateAge(patient.date_of_birth)}",
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "ID: ${patient.patient_id}",
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Location: ${patient.address}",
                                    fontSize = 18.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }

                        // Total Diagnosis Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = CardDefaults.cardColors(containerColor = blueColor),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "Total Diagnosis",
                                    fontSize = 20.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = state.diagnoses.size.toString(),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // Add Diagnosis Button
                        Button(
                            onClick = {
                                navController.navigate(NavigationRoutes.DIAGNOSIS_FORM + "?patientId=${patient.patient_id}")
                            },
                            modifier = Modifier
                                .padding(bottom = 12.dp)
                                .align(Alignment.Start)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = blueColor),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Add Diagnosis",
                                fontSize = 14.sp
                            )
                        }

                        // Search Field
                        OutlinedTextField(
                            value = "",
                            onValueChange = { },
                            placeholder = {
                                Text(
                                    text = "Search for Diagnosis...",
                                    color = Color.Gray
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = blueColor,
                                unfocusedBorderColor = blueColor,
                                focusedPlaceholderColor = Color.Gray,
                                unfocusedPlaceholderColor = Color.Gray
                            )
                        )

                        // Diagnoses Table
                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Table Header
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(blueColor)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "Diagnosis",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Date",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "Actions",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // Table Content
                            if (state.diagnoses.isEmpty()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(100.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "No diagnoses available",
                                        color = Color.Gray
                                    )
                                }
                            } else {
                                LazyColumn(
                                    modifier = Modifier.height(300.dp)
                                ) {
                                    items(state.diagnoses) { diagnosis ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = diagnosis.diagnosis_name,
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                text = formatDate(diagnosis.created_at),
                                                fontSize = 16.sp,
                                                textAlign = TextAlign.Center,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Button(
                                                onClick = {
                                                    navController.navigate("diagnosis_summary/${diagnosis.diagnosis_id}")
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = blueColor
                                                ),
                                                elevation = ButtonDefaults.buttonElevation(
                                                    defaultElevation = 2.dp
                                                ),
                                                modifier = Modifier
                                                    .weight(1f)
                                                    .height(40.dp)
                                            ) {
                                                Text(
                                                    text = "View Details",
                                                    fontSize = 14.sp
                                                )
                                            }
                                        }
                                        Divider(thickness = 0.5.dp, color = Color(0xFFD3D3D3))
                                    }
                                }
                            }
                        }

                        // Back Button
                        Button(
                            onClick = { navController.navigateUp() },
                            modifier = Modifier
                                .align(Alignment.CenterHorizontally)
                                .padding(top = 12.dp)
                                .height(40.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = blueColor),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                        ) {
                            Text(
                                text = "Back",
                                fontSize = 14.sp
                            )
                        }
                    } ?: run {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Patient not found",
                                color = Color.Red
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to calculate age from birth date
private fun calculateAge(birthDateString: String): Int {
    return try {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val birthDate = format.parse(birthDateString)
        val today = Calendar.getInstance()
        val birthCalendar = Calendar.getInstance()

        birthCalendar.time = birthDate!!

        var age = today.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

        if (today.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
            age--
        }

        age
    } catch (e: Exception) {
        0
    }
}

// Helper function to format date
private fun formatDate(dateString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        val outputFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        dateString
    }
}

