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

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var specialty by remember { mutableStateOf("") }

    LaunchedEffect(loginResult) {
        loginResult?.onSuccess {
            selectedRole?.role_id?.let { onLoginSuccess(it) }
        }
    }

    LaunchedEffect(signupResult) {
        signupResult?.onSuccess {
            selectedRole?.role_id?.let { onLoginSuccess(it) }
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF0FDFF)),
        color = Color(0xFFF0FDFF)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .widthIn(max = 400.dp)
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(Color.White)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // Tab-like Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 24.dp)
                            .height(48.dp)
                            .background(
                                color = Color(0xFFE3F2FD),
                                shape = RoundedCornerShape(24.dp)
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val tabModifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()

                        Button(
                            onClick = { isLoginTab = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isLoginTab) Color(0xFF2962FF) else Color.White,
                                contentColor = if (isLoginTab) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = tabModifier.padding(2.dp),
                            elevation = null,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("Login")
                        }

                        Button(
                            onClick = { isLoginTab = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!isLoginTab) Color(0xFF2962FF) else Color.White,
                                contentColor = if (!isLoginTab) Color.White else Color.Black
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = tabModifier.padding(2.dp),
                            elevation = null,
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Text("SignUp")
                        }
                    }

                    // Role dropdown
                    RoleSelector(
                        roles = roles,
                        selectedRole = selectedRole,
                        onRoleSelected = { viewModel.setSelectedRole(it) }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // SignUp only fields
                    if (!isLoginTab) {
                        CommonTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Name",
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CommonTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PasswordField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Create Password"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PasswordField(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password"
                        )
                    } else {
                        CommonTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = "Email",
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PasswordField(
                            value = password,
                            onValueChange = { password = it },
                            label = "Enter Password"
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    CommonButton(
                        text = if (isLoginTab) "Login" else "SignUp",
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
                        },
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
