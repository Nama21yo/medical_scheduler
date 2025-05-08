package com.example.medical_schedule_app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.example.medical_schedule_app.ui.components.*
import kotlinx.coroutines.launch
import java.lang.Math.log

@Composable
fun LoginSignupScreen(
    onLoginSuccess: (roleId: Int) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val roles by viewModel.roles.observeAsState(emptyList())
    val selectedRole by viewModel.selectedRole.observeAsState()
    val loginResult by viewModel.loginResult.observeAsState()
    val signupResult by viewModel.signupResult.observeAsState()

    var isLoginTab by remember { mutableStateOf(true) }

    // Form fields
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }

    // Handle login result
    LaunchedEffect(loginResult) {
        loginResult?.onSuccess {
            selectedRole?.role_id?.let { onLoginSuccess(it) }
        }
    }

    // Handle signup result
    LaunchedEffect(signupResult) {
        signupResult?.onSuccess {
            selectedRole?.role_id?.let { onLoginSuccess(it) }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFF5FBFF)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    // Toggle Login / SignUp Tabs
                    TabRow(
                        selectedTabIndex = if (isLoginTab) 0 else 1,
                        containerColor = Color.Transparent
                    ) {
                        Tab(
                            selected = isLoginTab,
                            onClick = { isLoginTab = true },
                            modifier = Modifier.background(
                                if (isLoginTab) Color(0xFF2962FF) else Color.White
                            )
                        ) {
                            Text(
                                text = "Login",
                                color = if (isLoginTab) Color.White else Color.Black,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Tab(
                            selected = !isLoginTab,
                            onClick = { isLoginTab = false },
                            modifier = Modifier.background(
                                if (!isLoginTab) Color(0xFF2962FF) else Color.White
                            )
                        ) {
                            Text(
                                text = "SignUp",
                                color = if (!isLoginTab) Color.White else Color.Black,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Role Selector (common to both)
                    RoleSelector(
                        roles = roles,
                        selectedRole = selectedRole,
                        onRoleSelected = { viewModel.setSelectedRole(it) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // SignUp only fields
                    if (!isLoginTab) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CommonTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = "Name",
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                modifier = Modifier.weight(1f)
                            )
                            CommonTextField(
                                value = specialty,
                                onValueChange = { specialty = it },
                                label = "Specialty",
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next,
                                modifier = Modifier.weight(1f)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Email (common)
                    CommonTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password (common)
                    PasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = if (isLoginTab) "Enter Password" else "Create Password"
                    )

                    // Confirm Password (signup only)
                    if (!isLoginTab) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Your Password"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Button
                    CommonButton(
                        text = if (isLoginTab) "Login" else "Create Account",
                        onClick = {
                            if (isLoginTab) {
                                viewModel.login(email, password)
                            } else {
                                if (password == confirmPassword && email.isNotBlank()) {
                                    selectedRole?.role_id?.let { roleId ->
                                        viewModel.signup(email, password, name, roleId, specialty)
                                    }
                                }
                            }
                        }
                        ,
                        isEnabled = email.isNotBlank() &&
                                (if (isLoginTab) password.isNotBlank()
                                else password == confirmPassword && name.isNotBlank()) &&
                                selectedRole != null
                    )
                }
            }
        }
    }
}
