package com.example.medical_schedule_app.ui.receptionist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Define QueueEntry data class
data class QueueEntry(
    val id: Int,
    val patientName: String,
    val status: String,
    val isActionable: Boolean = true
)

// Define colors
val DarkBlue = Color(0xFF0D253F)
val MediumBlue = Color(0xFF3D6FB4)
val LightBackgroundBlue = Color(0xFFF0F7FC)
val TextColorDark = Color(0xFF374151)
val TextColorLight = Color.White
val BorderColor = Color(0xFFD1D5DB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReceptionistQueueScreenPhone(
    onNavigateToAddPatient: () -> Unit,
    onLogout: () -> Unit,
    onUserProfileClick: () -> Unit
) {
    val activeEntriesCount by remember { mutableStateOf(0) }
    val pendingEntriesCount by remember { mutableStateOf(0) }
    var queueSearchQuery by remember { mutableStateOf("") }
    var dbSearchQuery by remember { mutableStateOf("") }
    var showMenu by remember { mutableStateOf(false) }

    val queueItems = remember {
        listOf(
            QueueEntry(1, "Abebe T.", "Not Pending", isActionable = true),
            QueueEntry(2, "Bekele C.", "Pending", isActionable = true),
            QueueEntry(3, "Chaltu M.", "Active", isActionable = true)
        )
    }

    val filteredQueueItems = remember(queueItems, queueSearchQuery) {
        if (queueSearchQuery.isBlank()) {
            queueItems
        } else {
            queueItems.filter {
                it.patientName.contains(queueSearchQuery, ignoreCase = true) ||
                        it.status.contains(queueSearchQuery, ignoreCase = true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Receptionist Queue", color = TextColorLight) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MediumBlue,
                    titleContentColor = TextColorLight,
                    actionIconContentColor = TextColorLight
                ),
                actions = {
                    IconButton(onClick = onUserProfileClick) {
                        Icon(
                            imageVector = Icons.Filled.AccountCircle,
                            contentDescription = "User Profile"
                        )
                    }
                    IconButton(onClick = { showMenu = !showMenu }) {
                        Icon(
                            imageVector = Icons.Filled.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Logout") },
                            onClick = {
                                showMenu = false
                                onLogout()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(LightBackgroundBlue)
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Stats Cards
            StatsCard(
                title = "Active Entries",
                count = activeEntriesCount,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatsCard(
                title = "Pending Entries",
                count = pendingEntriesCount,
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
                    .height(40.dp) // Reduced button height
            ) {
                Text("Add patient", color = TextColorLight, fontSize = 14.sp) // Adjusted font size
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
                        value = queueSearchQuery,
                        onValueChange = { queueSearchQuery = it },
                        placeholder = "Enter name or status...",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text("Search in Database", style = MaterialTheme.typography.titleSmall, color = TextColorDark)
                    Spacer(modifier = Modifier.height(4.dp))
                    SearchInputField(
                        value = dbSearchQuery,
                        onValueChange = { dbSearchQuery = it },
                        placeholder = "Search all patients...",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Queue Table
            Text("Current Queue", style = MaterialTheme.typography.titleMedium, color = TextColorDark, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            QueueTable(
                queueItems = filteredQueueItems,
                onResolveClick = { entryId ->
                    println("Resolve clicked for ID: $entryId")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

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
        placeholder = { Text(placeholder, fontSize = 14.sp) },
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
            disabledContainerColor = Color.White
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
                    Divider(color = BorderColor, thickness = 1.dp)
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
            Button(
                onClick = { onResolveClick(item.id) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MediumBlue),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.height(36.dp)
            ) {
                Text("Resolve", color = TextColorLight, fontSize = 13.sp)
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

@Preview(showBackground = true, device = "spec:shape=Normal,width=360,height=640,unit=dp,dpi=480")
@Composable
fun ReceptionistQueueScreenPhonePreview() {
    MaterialTheme {
        ReceptionistQueueScreenPhone(
            onNavigateToAddPatient = {},
            onLogout = {},
            onUserProfileClick = {}
        )
    }
}