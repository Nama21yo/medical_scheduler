package com.example.medical_schedule_app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
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
import com.example.medical_schedule_app.ui.components.MedicalAppBar // Assuming you have this


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(
    navController: NavController,
    viewModel: AddEmployeeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.addEmployeeSuccess) {
        if (state.addEmployeeSuccess) {
            navController.popBackStack()
            viewModel.onEvent(AddEmployeeEvent.ResetSuccessState)
        }
    }

    Scaffold(
        topBar = {
            MedicalAppBar(
                title = "Add Employee",
                navController = navController,
                showBackButton = true
            )
        },
        containerColor = Color(0xFFEBF5FF)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Employee",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF073B63),
                modifier = Modifier.padding(vertical = 24.dp)
            )

            // Name Field
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddEmployeeEvent.OnNameChanged(it)) },
                label = { Text("Name") },
                placeholder = { Text("Enter employee name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF073B63),
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(AddEmployeeEvent.OnEmailChanged(it)) },
                label = { Text("Email") },
                placeholder = { Text("Enter employee email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF073B63),
                    unfocusedBorderColor = Color.Gray
                ),
                isError = state.error?.contains("email", ignoreCase = true) == true
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Branch ID Field
            OutlinedTextField(
                value = if (state.branchId == 0) "" else state.branchId.toString(),
                onValueChange = {
                    val id = it.toIntOrNull() ?: 0
                    viewModel.onEvent(AddEmployeeEvent.OnBranchIdChanged(id))
                },
                label = { Text("Branch ID") },
                placeholder = { Text("Enter branch ID") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF073B63),
                    unfocusedBorderColor = Color.Gray
                )
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Role Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.selectedRole.ifEmpty { "Select Role" },
                    onValueChange = { },
                    label = { Text("Select Role") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onEvent(AddEmployeeEvent.ToggleRoleDropdown) },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            "Dropdown arrow",
                            Modifier.clickable { viewModel.onEvent(AddEmployeeEvent.ToggleRoleDropdown) }
                        )
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF073B63),
                        unfocusedBorderColor = Color.Gray
                    ),
                    isError = state.error?.contains("role", ignoreCase = true) == true
                )

                DropdownMenu(
                    expanded = state.showRoleDropdown,
                    onDismissRequest = { viewModel.onEvent(AddEmployeeEvent.DismissRoleDropdown) },
                    modifier = Modifier.fillMaxWidth(0.9f)
                ) {
                    state.roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role) },
                            onClick = {
                                viewModel.onEvent(AddEmployeeEvent.OnRoleSelected(role))
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }

            // Add Button
            Button(
                onClick = { viewModel.onEvent(AddEmployeeEvent.OnAddEmployeeClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF073B63)),
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Add Employee", fontSize = 16.sp)
                }
            }
        }
    }
}
