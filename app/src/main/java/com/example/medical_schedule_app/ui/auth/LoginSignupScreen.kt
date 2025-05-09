package com.example.medical_schedule_app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medical_schedule_app.ui.components.*
@Composable
fun LoginSignupScreen(
    onLoginSuccess: (roleId: Int) -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.loginSuccess) {
        if (state.loginSuccess) {
            state.selectedRole?.role_id?.let(onLoginSuccess)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize().background(Color(0xFFF0FDFF)),
        color = Color(0xFFF0FDFF)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Card(
                modifier = Modifier.widthIn(max = 400.dp).padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.background(Color.White).padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Toggle Tabs
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp).height(48.dp)
                            .background(Color(0xFFE3F2FD), RoundedCornerShape(24.dp)),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabModifier = Modifier.weight(1f).fillMaxHeight()

                        Button(
                            onClick = { viewModel.onEvent(AuthUiEvent.ToggleTab(true)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (state.isLogin) Color(0xFF2962FF) else Color.White,
                                contentColor = if (state.isLogin) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = tabModifier.padding(2.dp),
                            elevation = null,
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("Login") }

                        Button(
                            onClick = { viewModel.onEvent(AuthUiEvent.ToggleTab(false)) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!state.isLogin) Color(0xFF2962FF) else Color.White,
                                contentColor = if (!state.isLogin) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = tabModifier.padding(2.dp),
                            elevation = null,
                            contentPadding = PaddingValues(0.dp)
                        ) { Text("SignUp") }
                    }

                    // Role Selector
                    RoleSelector(
                        roles = state.roles,
                        selectedRole = state.selectedRole,
                        onRoleSelected = { viewModel.onEvent(AuthUiEvent.SelectRole(it)) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Fields
                    if (!state.isLogin) {
                        CommonTextField(state.name, { viewModel.onEvent(AuthUiEvent.UpdateName(it)) }, "Name")
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    CommonTextField(state.email, { viewModel.onEvent(AuthUiEvent.UpdateEmail(it)) }, "Email")
                    Spacer(modifier = Modifier.height(8.dp))

                    PasswordField(state.password, { viewModel.onEvent(AuthUiEvent.UpdatePassword(it)) }, if (state.isLogin) "Enter Password" else "Create Password")
                    if (!state.isLogin) {
                        Spacer(modifier = Modifier.height(8.dp))
                        PasswordField(state.confirmPassword, { viewModel.onEvent(AuthUiEvent.UpdateConfirmPassword(it)) }, "Confirm Password")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CommonButton(
                        text = if (state.isLogin) "Login" else "SignUp",
                        onClick = { viewModel.onEvent(AuthUiEvent.Submit) },
                        isEnabled = state.email.isNotBlank() && state.password.isNotBlank() &&
                                (if (state.isLogin) true else state.password == state.confirmPassword && state.name.isNotBlank()) &&
                                state.selectedRole != null
                    )
                }
            }
        }
    }
}

