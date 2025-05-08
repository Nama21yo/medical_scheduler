package com.example.medical_schedule_app.ui.auth

import android.util.Log
import android.widget.Toast // For error messages
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext // For Toasts
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medical_schedule_app.ui.components.CommonButton
import com.example.medical_schedule_app.ui.components.CommonTextField
import com.example.medical_schedule_app.ui.components.PasswordField
import com.example.medical_schedule_app.ui.components.RoleSelector

@Composable
fun LoginSignupScreen(
    onLoginSuccess: (roleId: Int) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val roles by viewModel.roles.observeAsState(emptyList())
    val selectedRole by viewModel.selectedRole.observeAsState() // Will be null after logout
    val loginResult by viewModel.loginResult.observeAsState()
    val signupResult by viewModel.signupResult.observeAsState()
    val context = LocalContext.current

    var isLoginTab by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) } // For progress indicator

    // This LaunchedEffect will run when the screen is first composed.
    // Its purpose is to check if the user is ALREADY logged in.
    // If so, it should navigate away immediately.
    LaunchedEffect(Unit) {
        val token = viewModel.sessionManager.fetchAuthToken()
        Log.d("LoginScreen", "Initial Composition: Token = $token, SelectedRole = ${selectedRole?.name}")
        if (token != null) {
            val roleIdFromSession = viewModel.sessionManager.fetchUserId() // Assuming role_id is saved as userId
            if (roleIdFromSession != null) {
                Log.d("LoginScreen", "Already logged in (token & roleId $roleIdFromSession found). Navigating.")
                onLoginSuccess(roleIdFromSession)
            } else {
                // Token exists but no role_id, this is an error state.
                // Clear the potentially bad token and force user to login screen.
                Log.e("LoginScreen", "Token exists but no roleId in session. Logging out to be safe.")
                viewModel.logout() // This will clear token and reset VM states
                Toast.makeText(context, "Session error. Please log in.", Toast.LENGTH_LONG).show()
            }
        } else {
            // No token, good. Stay on login screen.
            // Reset any lingering UI states if needed from previous attempts
            Log.d("LoginScreen", "No token. User needs to login/signup.")
            viewModel.clearAuthResults() // Clear any previous login/signup attempts
            isLoginTab = true // Default to login tab
            // email = "" // Optionally clear fields
            // password = ""
        }
    }


    // Handle successful login
    LaunchedEffect(loginResult) {
        loginResult?.let { result -> // Process only if result is not null
            isLoading = false // Stop loading
            result.onSuccess {
                // Token is already saved by ViewModel. Now fetch roleId for navigation.
                val roleIdFromSession = viewModel.sessionManager.fetchUserId()
                if (roleIdFromSession != null) {
                    Log.d("LoginScreen", "Login Result Success: RoleID $roleIdFromSession. Navigating.")
                    onLoginSuccess(roleIdFromSession)
                    viewModel.clearAuthResults() // Consume the event
                } else {
                    Log.e("LoginScreen", "Login Result Success but roleId not found in session after login!")
                    Toast.makeText(context, "Login successful, but role info missing. Please retry.", Toast.LENGTH_LONG).show()
                    viewModel.logout() // Log out to clear inconsistent state
                }
            }.onFailure { exception ->
                Log.e("LoginScreen", "Login Failed: ${exception.message}")
                Toast.makeText(context, "Login Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                viewModel.clearAuthResults() // Consume the event
            }
        }
    }

    // Handle successful signup
    LaunchedEffect(signupResult) {
        signupResult?.let { result -> // Process only if result is not null
            isLoading = false // Stop loading
            result.onSuccess {
                // Token is saved by ViewModel. Fetch roleId for navigation.
                val roleIdFromSession = viewModel.sessionManager.fetchUserId()
                if (roleIdFromSession != null) {
                    Log.d("LoginScreen", "Signup Result Success: RoleID $roleIdFromSession. Navigating.")
                    onLoginSuccess(roleIdFromSession)
                    viewModel.clearAuthResults() // Consume the event
                } else {
                    Log.e("LoginScreen", "Signup Result Success but roleId not found in session after signup!")
                    Toast.makeText(context, "Signup successful, but role info missing. Please retry.", Toast.LENGTH_LONG).show()
                    viewModel.logout() // Log out to clear inconsistent state
                }
            }.onFailure { exception ->
                Log.e("LoginScreen", "Signup Failed: ${exception.message}")
                Toast.makeText(context, "Signup Failed: ${exception.message}", Toast.LENGTH_LONG).show()
                viewModel.clearAuthResults() // Consume the event
            }
        }
    }


    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5FBFF) // Light blue background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        TabRow(
                            selectedTabIndex = if (isLoginTab) 0 else 1,
                            containerColor = Color.Transparent, // Make TabRow background transparent
                            divider = {} // Remove default divider
                        ) {
                            Tab(
                                selected = isLoginTab,
                                onClick = { isLoginTab = true },
                                modifier = Modifier.background(
                                    if (isLoginTab) Color(0xFF3D6FB4) else Color.Transparent, // MediumBlue
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                            ) {
                                Text(
                                    text = "Login",
                                    color = if (isLoginTab) Color.White else Color(0xFF3D6FB4),
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                                )
                            }
                            Tab(
                                selected = !isLoginTab,
                                onClick = { isLoginTab = false },
                                modifier = Modifier.background(
                                    if (!isLoginTab) Color(0xFF3D6FB4) else Color.Transparent, // MediumBlue
                                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                                )
                            ) {
                                Text(
                                    text = "SignUp",
                                    color = if (!isLoginTab) Color.White else Color(0xFF3D6FB4),
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        RoleSelector(
                            roles = roles,
                            selectedRole = selectedRole, // This will be null initially or after logout
                            onRoleSelected = { viewModel.setSelectedRole(it) },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        CommonTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        if (isLoginTab) {
                            PasswordField(
                                value = password,
                                onValueChange = { password = it },
                                label = "Password", // Changed label for consistency
                                imeAction = ImeAction.Done,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }


                        Spacer(modifier = Modifier.height(24.dp))

                        CommonButton(
                            text = if (isLoginTab) "Login" else "SignUp",
                            onClick = {
                                if (selectedRole == null) {
                                    Toast.makeText(context, "Please select a role", Toast.LENGTH_SHORT).show()
                                    return@CommonButton
                                }
                                isLoading = true
                                if (isLoginTab) {
                                    viewModel.login(email, password)
                                } else {
                                    // For signup, role_id is directly used
                                    selectedRole?.role_id?.let { roleId ->
                                        viewModel.signup(email, roleId)
                                    }
                                }
                            },
                            isEnabled = email.isNotBlank() &&
                                    ( (isLoginTab && password.isNotBlank()) || !isLoginTab ) &&
                                    selectedRole != null && !isLoading, // Disable while loading
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}