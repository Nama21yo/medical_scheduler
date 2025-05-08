package com.example.medical_schedule_app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.medical_schedule_app.data.models.User
import com.example.medical_schedule_app.navigation.NavigationRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    // Reload data when the screen is recomposed after returning from AddEmployee
    LaunchedEffect(navController.currentBackStackEntry) {
        viewModel.onEvent(AdminEvent.LoadDashboardData)
    }


    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar
        AdminSidebar(navController)

        // Main Content
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Admin", color = Color.White) }, // Text is "Admin" on top left of image
                    actions = {
                        IconButton(onClick = { /* TODO: Profile action */ }) {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Profile",
                                tint = Color.DarkGray, // Or a theme color
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFFEBF5FF) // Match content background
                    )
                )
            },
            containerColor = Color(0xFFEBF5FF) // Light blue background for main content area
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Admin Dashboard",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF073B63),
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Stats Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    StatCard("Total Doctors", state.totalDoctors.toString(), Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(16.dp))
                    StatCard("Total Receptionists", state.totalReceptionists.toString(), Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Add Employee Button
                Button(
                    onClick = { navController.navigate(NavigationRoutes.ADD_EMPLOYEE) },
                    modifier = Modifier
                        .fillMaxWidth(0.5f) // As per UI, not full width
                        .height(40.dp)
                        .align(Alignment.Start),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF073B63)),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text("Add Employee", fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Search Bar
                OutlinedTextField(
                    value = state.searchTerm,
                    onValueChange = { viewModel.onEvent(AdminEvent.OnSearchTermChanged(it)) },
                    label = { Text("Search for Employee ...") },
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = "Search") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF073B63),
                        unfocusedBorderColor = Color.Gray
                    ),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))

                if (state.isLoading && state.displayedUsers.isEmpty()) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else if (state.error != null) {
                    Text(
                        state.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else {
                    // Employee List Table
                    EmployeeTable(
                        users = state.displayedUsers,
                        onDeleteUser = { userId ->
                            viewModel.onEvent(AdminEvent.OnDeleteUserClicked(userId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AdminSidebar(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(70.dp) // Adjust width as needed
            .background(Color(0xFF073B63)) // Dark blue sidebar
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text(
            "Admin",
            color = Color.White,
            fontSize = 10.sp, // Small text as in image
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        // Sidebar Icons - Placeholder actions
        IconButton(onClick = { /* TODO: Dashboard action */ }) {
            Icon(Icons.Filled.GridView, contentDescription = "Dashboard", tint = Color.White)
        }
        IconButton(onClick = { /* TODO: Manage Users/Patients action */ }) {
            Icon(Icons.Filled.PeopleOutline, contentDescription = "Manage Users", tint = Color.White)
        }
        Spacer(Modifier.weight(1f)) // Pushes logout to bottom
        IconButton(onClick = {
            // Example Logout: Pop back to Auth screen
            navController.navigate(NavigationRoutes.AUTH) {
                popUpTo(NavigationRoutes.ADMIN_HOME) { inclusive = true }
            }
        }) {
            Icon(Icons.Filled.ExitToApp, contentDescription = "Logout", tint = Color.White)
        }
    }
}


@Composable
fun StatCard(title: String, count: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF3A7CA5)), // Blue card color
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
            Text(text = count, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun EmployeeTable(users: List<User>, onDeleteUser: (Int) -> Unit) {
    Column {
        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF3A7CA5)) // Header background
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Name", Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold)
            Text("Role", Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold)
            Text("Actions", Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }

        // Table Rows
        if (users.isEmpty()) {
            Text(
                "No users found.",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp
            )
        } else {
            LazyColumn {
                items(users, key = { it.user_id }) { user ->
                    EmployeeRow(user, onDeleteUser)
                    Divider()
                }
            }
        }
    }
}

@Composable
fun EmployeeRow(user: User, onDeleteUser: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(user.username, Modifier.weight(1f))
        Text(user.role.name, Modifier.weight(1f))
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Button(
                onClick = { onDeleteUser(user.user_id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF073B63), // Dark blue button
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp) // Smaller padding
            ) {
                Text("Delete User", fontSize = 12.sp)
            }
        }
    }
}

