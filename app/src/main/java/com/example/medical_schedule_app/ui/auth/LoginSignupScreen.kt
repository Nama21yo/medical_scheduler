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
import com.example.medical_schedule_app.ui.components.CommonButton
import com.example.medical_schedule_app.ui.components.CommonTextField
import com.example.medical_schedule_app.ui.components.PasswordField
import com.example.medical_schedule_app.ui.components.RoleSelector

@Composable
fun LoginSignupScreen(
    onLoginSuccess: (roleId: Int) -> Unit,
    viewModel: AuthViewModel = hiltViewModel() // Inject ViewModel using Hilt
) {
    val roles by viewModel.roles.observeAsState(emptyList())
    val selectedRole by viewModel.selectedRole.observeAsState()
    val loginResult by viewModel.loginResult.observeAsState()
    val signupResult by viewModel.signupResult.observeAsState()

    var isLoginTab by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Handle login result
    LaunchedEffect(loginResult) {
        loginResult?.onSuccess {
            selectedRole?.role_id?.let { roleId -> onLoginSuccess(roleId) }
        }
    }

    // Handle signup result
    LaunchedEffect(signupResult) {
        signupResult?.onSuccess {
            selectedRole?.role_id?.let { roleId -> onLoginSuccess(roleId) }
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

                    // Role Selector
                    RoleSelector(
                        roles = roles,
                        selectedRole = selectedRole,
                        onRoleSelected = { viewModel.setSelectedRole(it) }
                    )

                    // Email Field
                    CommonTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email",
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )

                    // Password Field (only in Login)
                    if (isLoginTab) {
                        PasswordField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Enter Password"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action Button
                    CommonButton(
                        text = if (isLoginTab) "Login" else "SignUp",
                        onClick = {
                            if (isLoginTab) {
                                viewModel.login(email, password)
                            } else {
                                selectedRole?.role_id?.let { roleId ->
                                    viewModel.signup(email, roleId)
                                }
                            }
                        },
                        isEnabled = email.isNotBlank() && (isLoginTab && password.isNotBlank() || !isLoginTab) && selectedRole != null
                    )
                }
            }
        }
    }
}
