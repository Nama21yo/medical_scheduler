
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.medical_schedule_app.navigation.NavigationRoutes
import com.example.medical_schedule_app.ui.components.MedicalAppBar
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

    LaunchedEffect(patientId) {
        if (patientId > 0) {
            viewModel.onEvent(PatientHistoryEvent.FetchPatientHistory(patientId))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF))
    ) {
        MedicalAppBar(
            title = "Patient History",
            navController = navController,
            showBackButton = true
        )

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
                    .padding(16.dp)
            ) {
                // Patient Info Card
                state.patient?.let { patient ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2962FF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Name: ${patient.first_name} ${patient.last_name}",
                                fontSize = 18.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Age: ${calculateAge(patient.date_of_birth)}",
                                fontSize = 16.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "ID: ${patient.patient_id}",
                                fontSize = 16.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Location: ${patient.address}",
                                fontSize = 16.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Total Diagnosis Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF2962FF))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Total Diagnosis",
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = state.diagnoses.size.toString(),
                                fontSize = 24.sp,
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
                            .padding(bottom = 16.dp)
                            .align(Alignment.Start),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                    ) {
                        Text("Add Diagnosis")
                    }

                    // Search Field (Non-functional in this implementation)
                    OutlinedTextField(
                        value = "",
                        onValueChange = { },
                        placeholder = { Text("Search for Diagnosis...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF2962FF),
                            unfocusedBorderColor = Color(0xFF2962FF)
                        )
                    )

                    // Diagnoses Table
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF2962FF))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "Diagnosis",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Date",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "Actions",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
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
                                            .padding(vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = diagnosis.diagnosis_name,
                                            fontSize = 16.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = formatDate(diagnosis.created_at),
                                            fontSize = 16.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Button(
                                            onClick = {
                                                // Navigate to view diagnosis details
                                                navController.navigate("diagnosis_summary/${diagnosisId}")
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFF2962FF)
                                            ),
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(36.dp)
                                        ) {
                                            Text(
                                                text = "View Details",
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                    Divider()
                                }
                            }
                        }
                    }

                    // Back Button
                    Button(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0D47A1))
                    ) {
                        Text("Back")
                    }
                } ?: run {
                    // If patient is null
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