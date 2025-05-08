package com.example.medical_schedule_app.ui.diagnosis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.medical_schedule_app.data.models.responses.DiagnosisResponse
import com.example.medical_schedule_app.ui.doctor.PatientHistoryEvent.FetchPatientHistory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisSummaryScreen(
    diagnosisId: String,
    navController: NavController,
    viewModel: DiagnosisSummaryViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(diagnosisId) {
        viewModel.fetchDiagnosisSummary(diagnosisId)
    }

    when {
        state.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        state.diagnosis != null -> {
            DiagnosisContent(diagnosis = state.diagnosis!!, navController = navController)
        }

        state.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = state.error!!, color = Color.Red)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisContent(
    diagnosis: DiagnosisDetails,
    navController: NavController
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnosis Page") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2962FF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xF0F5FBFF)) // Lighter background
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Diagnosis Details",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .defaultMinSize(minHeight = 200.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF2F5E91)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    InfoLine("Diagnosis Name:", diagnosis.diagnosis)
                    InfoLine("Date:", diagnosis.date)
                    InfoLine("Doctor’s Name:", "Dr. Dagim") // Replace with real doctor data if available
                    InfoLine("Medication:", diagnosis.medication)
                    InfoLine("Comments:", diagnosis.notes)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF083B66))
            ) {
                Text("Back", color = Color.White)
            }
        }
    }
}

@Composable
fun InfoLine(label: String, value: String) {
    Text(
        text = "$label  $value",
        fontSize = 16.sp,
        color = Color.White
    )
}


@Composable
fun DiagnosisSection(
    title: String,
    content: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2962FF)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = content,
                fontSize = 16.sp
            )
        }
    }
}



data class DiagnosisDetails(
    val id: String,
    val patientName: String,
    val patientDetails: String,
    val date: String,
    val time: String,
    val symptoms: String,
    val diagnosis: String,
    val medication: String,
    val notes: String,
    val followUp: String
)



fun DiagnosisResponse.toDiagnosisDetails(): DiagnosisDetails {
    val (date, time) = created_at.split("T").let {
        val datePart = it[0]
        val timePart = it.getOrNull(1)?.substring(0, 5) ?: "N/A"
        datePart to timePart

    }

    return DiagnosisDetails(
        id = diagnosis_id.toString(),
        patientName = "${patient.first_name} ${patient.last_name}",
        patientDetails = "${patient.gender}, age unknown", // Use DOB calc if needed
        date = date,
        time = time,
        symptoms = diagnosis_details,
        diagnosis = diagnosis_name,
        medication = prescription,
        notes = "No additional notes provided.",
        followUp = "Follow up not scheduled."
    )
}
