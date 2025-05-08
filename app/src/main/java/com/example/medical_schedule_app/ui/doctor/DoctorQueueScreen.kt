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
    import androidx.compose.ui.text.style.TextAlign
    import androidx.compose.ui.tooling.preview.Preview
    import androidx.compose.ui.unit.dp
    import androidx.compose.ui.unit.sp
    import androidx.hilt.navigation.compose.hiltViewModel
    import androidx.lifecycle.ViewModel
    import androidx.navigation.NavController
    import androidx.navigation.compose.rememberNavController
    import com.example.medical_schedule_app.navigation.NavigationRoutes
    import com.example.medical_schedule_app.ui.components.MedicalAppBar
    import kotlinx.coroutines.flow.MutableStateFlow
    import kotlinx.coroutines.flow.StateFlow
    import kotlinx.coroutines.flow.asStateFlow


    @Composable
    fun DoctorQueueScreen(
        navController: NavController,
        viewModel: DoctorQueueViewModel = hiltViewModel()
    ) {
        val state by viewModel.state.collectAsState()
        val blueColor = Color(0xFF3D6FB4)

        MedicalAppBar(
            navController = navController,
            screenTitle = "Doctor Queue"
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF0F7FC))
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Adjusted for sidebar
            ) {
                // Stats Cards
                listOf(
                    "Total Completed" to state.totalCompleted,
                    "Pending" to state.pending,
                    "Resolved Pending" to state.resolvedPending
                ).forEach { (title, value) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = blueColor),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
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
                        .padding(top = 16.dp)
                ) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(blueColor)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Name",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "Actions",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center,
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
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.queues) { queue ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    // Name
                                    Text(
                                        text = "${queue.patient.first_name} ${queue.patient.last_name}",
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
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
                                        fontSize = 14.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.weight(1f)
                                    )

                                    // Actions
                                    Column(
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(horizontal = 8.dp),
                                        verticalArrangement = Arrangement.spacedBy(8.dp),
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
                                                containerColor = blueColor
                                            ),
                                            elevation = ButtonDefaults.buttonElevation(
                                                defaultElevation = 2.dp
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(36.dp)
                                        ) {
                                            Text(
                                                text = if (queue.status != 2) "Pend" else "Unpend",
                                                fontSize = 12.sp
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
                                                containerColor = blueColor
                                            ),
                                            elevation = ButtonDefaults.buttonElevation(
                                                defaultElevation = 2.dp
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(36.dp)
                                        ) {
                                            Text(
                                                text = "View History",
                                                fontSize = 12.sp
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                viewModel.onEvent(
                                                    DoctorQueueEvent.UpdateQueueStatus(queue.queue_id, 3)
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = blueColor
                                            ),
                                            elevation = ButtonDefaults.buttonElevation(
                                                defaultElevation = 2.dp
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(36.dp)
                                        ) {
                                            Text(
                                                text = "Complete",
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                Divider(thickness = 0.5.dp, color = Color(0xFFD3D3D3))
                            }
                        }
                    }
                }
            }
        }
    }


