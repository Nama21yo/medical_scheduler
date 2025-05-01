package com.example.medical_schedule_app.ui.doctor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.medical_schedule_app.navigation.NavigationRoutes
import com.example.medical_schedule_app.ui.components.MedicalAppBar

@Composable
fun DoctorQueueScreen(
    navController: NavController,
    viewModel: DoctorQueueViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0F8FF))
    ) {
        MedicalAppBar(
            title = "Doctor Queue",
            navController = navController,
            showBackButton = false
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Doctor Queue",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Stats Cards
            listOf(
                "Total Completed" to state.totalCompleted,
                "Pending" to state.pending,
                "Resolved Pending" to state.resolvedPending
            ).forEach { (title, value) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF2962FF))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = title,
                            fontSize = 14.sp,
                            color = Color.White
                        )
                        Text(
                            text = value.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            // Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2962FF))
                        .padding(6.dp)
                ) {
                    Text(
                        text = "Name",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Status",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Actions",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Table Content
                if (state.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (state.error != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error: ${state.error}",
                            color = Color.Red
                        )
                    }
                } else {
                    LazyColumn {
                        items(state.queues) { queue ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                // Name
                                Text(
                                    text = "${queue.patient.first_name} ${queue.patient.last_name}",
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                // Status
                                Text(
                                    text = when (queue.status) {
                                        1 -> "Not Pending"
                                        2 -> "Pending"
                                        3 -> "Resolved"
                                        else -> "Unknown"
                                    },
                                    fontSize = 13.sp,
                                    modifier = Modifier.weight(1f)
                                )

                                // Actions
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(horizontal = 4.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.onEvent(
                                                DoctorQueueEvent.UpdateQueueStatus(
                                                    queue.queue_id,
                                                    if (queue.status != 2) 2 else 1
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2962FF)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                    ) {
                                        Text(
                                            text = if (queue.status != 2) "Pend" else "Unpend",
                                            fontSize = 11.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            navController.navigate(
                                                NavigationRoutes.DIAGNOSIS_DETAILS.replace(
                                                    "{diagnosisId}",
                                                    queue.patient.patient_id.toString()
                                                )
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2962FF)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                    ) {
                                        Text(
                                            text = "View History",
                                            fontSize = 11.sp
                                        )
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.onEvent(
                                                DoctorQueueEvent.UpdateQueueStatus(queue.queue_id, 3)
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF2962FF)
                                        ),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(32.dp)
                                    ) {
                                        Text(
                                            text = "Complete",
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                            Divider(thickness = 0.5.dp)
                        }
                    }
                }
            }
        }
    }
}
