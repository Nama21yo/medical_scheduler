package com.example.medical_schedule_app.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack // Import for back button
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
// MedicalAppBar import is no longer needed for this screen's structure
// import com.example.medical_schedule_app.ui.components.MedicalAppBar
import com.example.medical_schedule_app.ui.components.MediumBlue // Still used for colors
import com.example.medical_schedule_app.ui.components.ScreenBackgroundColor // Still used for colors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEmployeeScreen(
    navController: NavController,
    viewModel: AddEmployeeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(state.addEmployeeSuccess) {
        if (state.addEmployeeSuccess) {
            // Optional: Show a toast or snackbar here for success
            navController.popBackStack()
            viewModel.onEvent(AddEmployeeEvent.ResetSuccessState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Add Employee",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MediumBlue, // Consistent with MedicalAppBar's theme
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        containerColor = ScreenBackgroundColor // Background for the content area
    ) { paddingValuesFromScaffold ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValuesFromScaffold) // Apply padding from the Scaffold
                .padding(16.dp), // Additional screen-specific padding
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add New Employee",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = MediumBlue,
                modifier = Modifier.padding(vertical = 24.dp)
            )

            val textFieldColors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MediumBlue,
                unfocusedBorderColor = MediumBlue.copy(alpha = 0.6f),
                focusedLabelColor = MediumBlue,
                cursorColor = MediumBlue,
                focusedTextColor = MaterialTheme.colorScheme.onSurface, // Or specific color if needed
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface // Or specific color if needed
            )

            // Name Field
            OutlinedTextField(
                value = state.name,
                onValueChange = { viewModel.onEvent(AddEmployeeEvent.OnNameChanged(it)) },
                label = { Text("Full Name") },
                placeholder = { Text("Enter employee's full name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Email Field
            OutlinedTextField(
                value = state.email,
                onValueChange = { viewModel.onEvent(AddEmployeeEvent.OnEmailChanged(it)) },
                label = { Text("Email Address") },
                placeholder = { Text("Enter employee's email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors,
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
                placeholder = { Text("Enter branch ID (e.g., 1)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = textFieldColors
                // Consider adding keyboardType = KeyboardType.Number
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Role Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = state.selectedRole.ifEmpty { "Select Role" },
                    onValueChange = { },
                    label = { Text("Role") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onEvent(AddEmployeeEvent.ToggleRoleDropdown) },
                    readOnly = true,
                    trailingIcon = {
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            "Select Role",
                            Modifier.clickable { viewModel.onEvent(AddEmployeeEvent.ToggleRoleDropdown) }
                        )
                    },
                    colors = textFieldColors,
                    isError = state.error?.contains("role", ignoreCase = true) == true
                )

                DropdownMenu(
                    expanded = state.showRoleDropdown,
                    onDismissRequest = { viewModel.onEvent(AddEmployeeEvent.DismissRoleDropdown) },
                    modifier = Modifier
                        .fillMaxWidth(0.9f) // Anchor to the TextField width approximately
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    state.roles.forEach { role ->
                        DropdownMenuItem(
                            text = { Text(role, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            // Add Button
            Button(
                onClick = { viewModel.onEvent(AddEmployeeEvent.OnAddEmployeeClicked) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MediumBlue),
                enabled = !state.isLoading,
                shape = RoundedCornerShape(8.dp)
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Add Employee", fontSize = 16.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}