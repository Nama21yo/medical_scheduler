package com.example.medical_schedule_app.ui.diagnosis

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.medical_schedule_app.ui.theme.Medical_schedule_appTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiagnosisSummaryScreen(
    diagnosisId: String,
    navController: NavController
) {

//    To be Implemented
    val diagnosis = remember {
        DiagnosisDetails(
            id = diagnosisId,
            patientName = "Sisay Tadewos",
            patientDetails = "Male, 45",
            date = "April 22, 2025",
            time = "10:15 AM",
            symptoms = "Frequent headaches, dizziness, and elevated blood pressure readings over the past week.",
            diagnosis = "Hypertension - Stage 1",
            medication = "Lisinopril 10mg - 1 tablet daily in the morning",
            notes = "Patient reports stress at work. Advised on stress management techniques and regular blood pressure monitoring.",
            followUp = "Follow up appointment in 2 weeks. Patient should bring blood pressure readings."
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Diagnosis Summary") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2962FF),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                actions = {
                    IconButton(onClick = { /* Edit diagnosis */ }) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5FBFF))
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = diagnosis.patientName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = diagnosis.patientDetails,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = diagnosis.date,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = diagnosis.time,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            DiagnosisSection("Symptoms", diagnosis.symptoms)
            DiagnosisSection("Diagnosis", diagnosis.diagnosis)
            DiagnosisSection("Prescribed Medication", diagnosis.medication)
            DiagnosisSection("Additional Notes", diagnosis.notes)
            DiagnosisSection("Follow Up", diagnosis.followUp)

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedButton(
                    onClick = { /* Generate PDF logic */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF2962FF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.PictureAsPdf,
                        contentDescription = "PDF",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Export PDF")
                }

                Button(
                    onClick = { /* Share logic */ },
                    modifier = Modifier
                        .weight(1f)
                        .height(50.dp),
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2962FF)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share")
                }
            }
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

@Preview(showBackground = true)
@Composable
fun DiagnosisSummaryScreenPreview() {
    Medical_schedule_appTheme {
        DiagnosisSummaryScreen(
            diagnosisId = "123",
            navController = rememberNavController()
        )
    }
}
