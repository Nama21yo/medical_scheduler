package com.example.medical_schedule_app.ui.receptionist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // Import items function
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.hilt.navigation.compose.hiltViewModel // Import Hilt ViewModel
import androidx.navigation.NavController
//import androidx.navigation.compose.rememberNavController
import com.example.medical_schedule_app.data.models.responses.PatientResponse
import com.example.medical_schedule_app.ui.components.MedicalAppBar
import com.example.medical_schedule_app.ui.auth.AuthViewModel // Required by MedicalAppBar

data class QueueEntry(
    val id: Int,
    val patientName: String,
    val status: String,
    val isActionable: Boolean = true
)

val DarkBlue = Color(0xFF0D253F)
val MediumBlue = Color(0xFF3D6FB4)
val LightBackgroundBlue = Color(0xFFF0F7FC)
val TextColorDark = Color(0xFF374151)
val TextColorLight = Color.White
val BorderColor = Color(0xFFD1D5DB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptionistQueueScreenPhone(
    navController: NavController,
    state: ReceptionistQueueState,
    onEvent: (ReceptionistQueueEvent) -> Unit, // Receive event handler
    onNavigateToAddPatient: () -> Unit,
    authViewModel: AuthViewModel, // Added for MedicalAppBar
) {
    MedicalAppBar(
        navController = navController,
        screenTitle = "Receptionist Queue",
        showBackButton = false, // Typically false for a main screen like this
        authViewModel = authViewModel,
        content = { paddingValuesFromMedicalAppBar -> // These paddingValues account for the TopAppBar
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(LightBackgroundBlue) // Content area background
                    .padding(paddingValuesFromMedicalAppBar) // Apply padding from MedicalAppBar
                    .padding(horizontal = 16.dp, vertical = 12.dp) // Additional content-specific padding
                    .verticalScroll(rememberScrollState())
            ) {
                // Stats Cards - Use counts from the ViewModel state
                StatsCard(
                    title = "Active Entries",
                    count = state.activeEntries,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatsCard(
                    title = "Pending Entries",
                    count = state.pendingEntries,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Add Patient Button
                Button(
                    onClick = onNavigateToAddPatient,
                    colors = ButtonDefaults.buttonColors(containerColor = DarkBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                ) {
                    Text("Add patient", color = TextColorLight, fontSize = 14.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Search Inputs - Side by Side
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Search in Current Queue", style = MaterialTheme.typography.titleSmall, color = TextColorDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        SearchInputField(
                            value = state.searchQueue, // Use state from ViewModel
                            onValueChange = { onEvent(ReceptionistQueueEvent.OnSearchQueueChange(it)) }, // Trigger event
                            placeholder = "Enter name or status...",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Search in Database", style = MaterialTheme.typography.titleSmall, color = TextColorDark)
                        Spacer(modifier = Modifier.height(4.dp))
                        SearchInputField(
                            value = state.searchDataBaseSearch, // Use state from ViewModel
                            onValueChange = { onEvent(ReceptionistQueueEvent.OnSearchDataBaseSearchChange(it)) }, // Trigger event
                            placeholder = "Search all patients...",
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Queue Table
                Text("Current Queue", style = MaterialTheme.typography.titleMedium, color = TextColorDark, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))

                // Map QueueResponse to QueueEntry for UI display
                val displayedQueueEntries = state.displayedQueues.map { queueResponse ->
                    QueueEntry(
                        id = queueResponse.queue_id,
                        patientName = (queueResponse.patient.first_name + " " + queueResponse.patient.last_name),
                        status = when (queueResponse.status) {
                            1 -> "Active"
                            2 -> "Pending"
                            3 -> "Resolved"
                            else -> "Unknown"
                        },
                        isActionable = queueResponse.status != 3 // Assuming you only want to action on non-resolved
                    )
                }

                QueueTable(
                    queueItems = displayedQueueEntries, // Use the displayed/filtered list from state
                    onResolveClick = { entryId ->
                        // Find the corresponding QueueResponse and trigger update status event
                        val queueToUpdate = state.displayedQueues.find { it.queue_id == entryId }
                        queueToUpdate?.let {
                            onEvent(ReceptionistQueueEvent.UpdateQueueStatus(it.queue_id, 3))
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // Display Database Search Results
                if (state.searchDataBaseSearch.isNotBlank()) {
                    Spacer(modifier = Modifier.height(20.dp))
                    Text("Database Search Results", style = MaterialTheme.typography.titleMedium, color = TextColorDark, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    if (state.isDatabaseSearchLoading) {
                        CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    } else if (state.databaseSearchError != null) {
                        Text(
                            text = "Error: ${state.databaseSearchError}",
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center
                        )
                    } else if (state.patients.isEmpty()) {
                        Text(
                            text = "No patients found in database.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .background(Color.White),
                            textAlign = TextAlign.Center,
                            color = TextColorDark
                        )
                    } else {
                        // Display database search results (e.g., in a LazyColumn)
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(max = 200.dp) // Limit height for scrolling
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.White)
                        ) {
                            items(state.patients, key = { patient -> patient.patient_id }) { patient ->
                                DatabasePatientResultItem(
                                    patient = patient,
                                    onAddToQueueClick = { onEvent(ReceptionistQueueEvent.AddToQueue(it)) }
                                )
                                HorizontalDivider(color = BorderColor, thickness = 1.dp)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp)) // Ensure content can scroll past bottom bar if any
            }
        }
    )
}

@Composable
fun DatabasePatientResultItem(patient: PatientResponse, onAddToQueueClick:(Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = (patient.first_name + " " + patient.last_name), fontSize = 18.sp, color = TextColorDark, fontWeight = FontWeight.Bold)
            Text(text = "ID: ${patient.patient_id}", color = TextColorDark)
            Text(text = "Phone: ${patient.phone_number}", color = TextColorDark)
            Text(text = "Email: ${patient.email}", color = TextColorDark)
        }
        Button(
            onClick = { onAddToQueueClick(patient.patient_id) },
            colors = ButtonDefaults.buttonColors(containerColor = MediumBlue),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text("Add to Queue", color = TextColorLight)
        }
        // Add an action button if needed, e.g., "Add to Queue"
        // Button(onClick = { /* Add to queue logic */ }) { Text("Add to Queue") }
    }
}

// Keep helper composables as they are pure UI elements
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchInputField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, fontSize = 14.sp, color = TextColorDark.copy(alpha = 0.7f)) },
        modifier = modifier
            .height(50.dp)
            .background(Color.White, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedTextColor = TextColorDark,
            unfocusedTextColor = TextColorDark
        )
    )
}

@Composable
fun StatsCard(title: String, count: Int, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MediumBlue),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, fontSize = 18.sp, color = TextColorLight, fontWeight = FontWeight.SemiBold)
            Text(text = count.toString(), fontSize = 28.sp, color = TextColorLight, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun QueueTable(
    queueItems: List<QueueEntry>,
    onResolveClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.clip(RoundedCornerShape(8.dp))) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MediumBlue)
                .padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            TableCell(text = "Name", weight = 0.35f, color = TextColorLight, isHeader = true)
            TableCell(text = "Status", weight = 0.35f, color = TextColorLight, isHeader = true)
            TableCell(text = "Actions", weight = 0.3f, color = TextColorLight, isHeader = true, alignment = TextAlign.Center)
        }

        if (queueItems.isEmpty()) {
            Text(
                text = "No patients in queue.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color.White),
                textAlign = TextAlign.Center,
                color = TextColorDark
            )
        } else {
            Column(modifier = Modifier.background(Color.White)) {
                queueItems.forEach { item ->
                    QueueTableRow(item = item, onResolveClick = onResolveClick)
                    HorizontalDivider(color = BorderColor, thickness = 1.dp)
                }
            }
        }
    }
}

@Composable
fun QueueTableRow(item: QueueEntry, onResolveClick: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TableCell(text = item.patientName, weight = 0.35f)
        TableCell(text = item.status, weight = 0.35f)
        Box(
            modifier = Modifier.weight(0.3f),
            contentAlignment = Alignment.Center
        ) {
            if (item.isActionable) { // Only show button if actionable
                Button(
                    onClick = { onResolveClick(item.id) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MediumBlue),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text("Resolve", color = TextColorLight, fontSize = 13.sp)
                }
            } else {
                Spacer(modifier = Modifier.height(36.dp)) // Maintain spacing
            }
        }
    }
}

@Composable
fun RowScope.TableCell(
    text: String,
    weight: Float,
    color: Color = TextColorDark,
    isHeader: Boolean = false,
    alignment: TextAlign = TextAlign.Left
) {
    Text(
        text = text,
        modifier = Modifier
            .weight(weight)
            .padding(vertical = 4.dp, horizontal = 6.dp),
        fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal,
        color = color,
        textAlign = alignment,
        fontSize = if (isHeader) 15.sp else 14.sp
    )
}
