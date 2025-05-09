package com.example.medical_schedule_app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.* // Keep for Search icon, etc.
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
// import androidx.compose.ui.draw.clip // Not used directly in AdminScreen after refactor
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.medical_schedule_app.data.models.User
import com.example.medical_schedule_app.navigation.NavigationRoutes
// Import MedicalAppBar and AuthViewModel
import com.example.medical_schedule_app.ui.components.MedicalAppBar
import com.example.medical_schedule_app.ui.auth.AuthViewModel

// Define colors specific to Admin content if they differ from MedicalAppBar's theme
val AdminContentBackgroundColor = Color(0xFFEBF5FF)
val AdminPrimaryTextColor = Color(0xFF073B63)
val AdminStatCardColor = Color(0xFF3A7CA5)
val AdminButtonColor = Color(0xFF073B63)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(
    navController: NavController,
    viewModel: AdminViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel() // Added for MedicalAppBar
) {
    val state by viewModel.state.collectAsState()

    // Reload data when the screen is recomposed after returning from AddEmployee
    LaunchedEffect(navController.currentBackStackEntry) {
        // Only reload if we are actually on the admin screen or coming back to it.
        // This check might need adjustment based on your exact navigation graph.
        if (navController.currentDestination?.route == NavigationRoutes.ADMIN_HOME) {
            viewModel.onEvent(AdminEvent.LoadDashboardData)
        }
    }

    MedicalAppBar(
        navController = navController,
        screenTitle = "Admin Dashboard",
        authViewModel = authViewModel,
        showBackButton = false // Typically false for a main dashboard screen
    ) { paddingValuesFromMedicalAppBar ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AdminContentBackgroundColor) // Light blue background for main content area
                .padding(paddingValuesFromMedicalAppBar) // Apply padding from MedicalAppBar
                .padding(16.dp) // Additional content-specific padding
        ) {
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
                    .fillMaxWidth(0.5f)
                    .height(40.dp)
                    .align(Alignment.Start),
                colors = ButtonDefaults.buttonColors(containerColor = AdminButtonColor),
                shape = MaterialTheme.shapes.small
            ) {
                Text("Add Employee", fontSize = 14.sp, color = Color.White)
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
                    focusedBorderColor = AdminPrimaryTextColor,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = AdminPrimaryTextColor,
                    unfocusedTextColor = AdminPrimaryTextColor,
                    cursorColor = AdminPrimaryTextColor,
                    focusedLabelColor = AdminPrimaryTextColor,
                    unfocusedLabelColor = Color.Gray,
                    focusedLeadingIconColor = AdminPrimaryTextColor,
                    unfocusedLeadingIconColor = Color.Gray
                ),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading && state.displayedUsers.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally), color = AdminPrimaryTextColor)
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

// AdminSidebar is no longer needed as its functionality is absorbed by MedicalAppBar
// @Composable
// fun AdminSidebar(navController: NavController) { ... }


@Composable
fun StatCard(title: String, count: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.height(100.dp),
        colors = CardDefaults.cardColors(containerColor = AdminStatCardColor), // Blue card color
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
                .background(AdminStatCardColor) // Header background (using StatCard color for consistency)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Name", Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold)
            Text("Role", Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold)
            Text("Actions", Modifier.weight(1f), color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        }

        // Table Rows
        if (users.isEmpty()) { // Adjusted condition for clarity
            Text(
                "No users found matching your search.",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = AdminPrimaryTextColor.copy(alpha = 0.7f)
            )
        } else if (users.isEmpty()){
            Text(
                "No users available.",
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp),
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                color = AdminPrimaryTextColor.copy(alpha = 0.7f)
            )
        }
        else {
            LazyColumn(modifier = Modifier.fillMaxHeight()) { // Allow table to take remaining height
                items(users, key = { it.user_id }) { user ->
                    EmployeeRow(user, onDeleteUser)
                    HorizontalDivider(color = AdminContentBackgroundColor) // Divider color matching background
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
            .background(Color.White) // Each row has a white background
            .padding(horizontal = 8.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(user.username, Modifier.weight(1f), color = AdminPrimaryTextColor)
        Text(user.role.name, Modifier.weight(1f), color = AdminPrimaryTextColor)
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
            Button(
                onClick = { onDeleteUser(user.user_id) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AdminButtonColor,
                    contentColor = Color.White
                ),
                shape = MaterialTheme.shapes.small,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text("Delete User", fontSize = 12.sp)
            }
        }
    }
}

// Dummy state for preview (if needed, remember to provide AuthViewModel for MedicalAppBar)
// @Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
// @Composable
// fun AdminScreenPreview() {
//    val navController = rememberNavController()
//    // You'd need to create dummy ViewModels or use a Hilt preview setup
//    // AdminScreen(navController = navController, viewModel = dummyAdminViewModel, authViewModel = dummyAuthViewModel)
// }