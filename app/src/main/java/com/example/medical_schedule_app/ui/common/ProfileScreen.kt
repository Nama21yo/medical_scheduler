package com.example.medical_schedule_app.ui.common

import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
//import androidx.compose.material.icons.filled.ArrowBack
//import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.medical_schedule_app.data.models.User
import com.example.medical_schedule_app.ui.auth.AuthViewModel


val ScreenBackgroundColor = Color(0xFFF0F7FC) // As used in DoctorQueueScreen
val CardBackgroundColor = Color.White // For cards on the ScreenBackgroundColor
val MediumBlue = Color(0xFF3D6FB4)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigateToAuth: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.updateSuccess, uiState.updateError, uiState.fetchError) {
        if (uiState.updateSuccess) {
            Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            viewModel.onEvent(ProfileEvent.ClearMessages) // Clear the success flag
            onNavigateBack() // Navigate to the previous screen
        }
        uiState.updateError?.let {
            Toast.makeText(context, "Update failed: $it", Toast.LENGTH_LONG).show()
            viewModel.onEvent(ProfileEvent.ClearMessages)
        }
        uiState.fetchError?.let {
            Toast.makeText(context, "Error: $it", Toast.LENGTH_LONG).show()
            viewModel.onEvent(ProfileEvent.ClearMessages)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isEditing) "Edit Profile" else "Profile") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { // This already uses onNavigateBack
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        authViewModel.logout()
                        onNavigateToAuth()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MediumBlue,
                    titleContentColor = Color.White,
                )
            )
        },
        containerColor = ScreenBackgroundColor // Apply the new background color
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileTabs(
                isEditing = uiState.isEditing,
                onProfileTabClick = { if (uiState.isEditing) viewModel.onEvent(ProfileEvent.ToggleEditMode) },
                onEditProfileTabClick = { if (!uiState.isEditing) viewModel.onEvent(ProfileEvent.ToggleEditMode) }
            )

            Spacer(modifier = Modifier.height(20.dp))

            when {
                uiState.isLoading && uiState.user == null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = MediumBlue)
                    }
                }
                uiState.user != null -> {
                    if (uiState.isEditing) {

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardBackgroundColor, // White card background
                            shadowElevation = 2.dp
                        ) {
                            EditProfileView(uiState = uiState, onEvent = viewModel::onEvent)
                        }
                    } else {
                        // UserProfileView will also be a card
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            color = CardBackgroundColor, // White card background
                            shadowElevation = 2.dp
                        ) {
                            UserProfileView(user = uiState.user!!)
                        }
                    }
                }
                uiState.fetchError != null -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Could not load profile.", color = MaterialTheme.colorScheme.error)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.onEvent(ProfileEvent.FetchUserProfile) },
                                colors = ButtonDefaults.buttonColors(containerColor = MediumBlue)
                            ) {
                                Text("Retry", color = Color.White)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp), // Or RoundedCornerShape(50) for consistency with other buttons
                colors = ButtonDefaults.buttonColors(
                    containerColor = MediumBlue, // Use MediumBlue for this button
                    contentColor = Color.White
                )
            ) {
                Text("Back to Home", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun ProfileTabs(
    isEditing: Boolean,
    onProfileTabClick: () -> Unit,
    onEditProfileTabClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(CardBackgroundColor, RoundedCornerShape(50)) // White background for tabs container
            .border(1.dp, MediumBlue, RoundedCornerShape(50)),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        TabButton(
            text = "Profile",
            selected = !isEditing,
            onClick = onProfileTabClick,
            modifier = Modifier.weight(1f)
        )
        TabButton(
            text = "Edit Profile",
            selected = isEditing,
            onClick = onEditProfileTabClick,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun TabButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) MediumBlue else Color.Transparent
    val textColor = if (selected) Color.White else MediumBlue

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = background,
            contentColor = textColor
        ),
        shape = RoundedCornerShape(50),
        modifier = modifier
            .height(36.dp)
    ) {
        Text(text, fontSize = 14.sp)
    }
}

@Composable
fun UserProfileView(user: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MediumBlue) // Solid MediumBlue background for avatar
        ) {
            Text(
                text = user.username.firstOrNull()?.toString()?.uppercase() ?: "U",
                modifier = Modifier.align(Alignment.Center),
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White // White text on MediumBlue background
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = user.username,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = MediumBlue // Username text color
        )
    }
}

@Composable
fun EditProfileView(uiState: ProfileState, onEvent: (ProfileEvent) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()

            .padding(horizontal = 16.dp, vertical = 24.dp), // Added more vertical padding
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val textFieldColors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MediumBlue,
            unfocusedBorderColor = MediumBlue.copy(alpha = 0.7f),
            focusedLabelColor = MediumBlue,
            cursorColor = MediumBlue,

            focusedTextColor = MaterialTheme.colorScheme.onSurface, // Default text color
            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            // Add other color customizations if needed
        )

        OutlinedTextField(
            value = uiState.editUsername,
            onValueChange = { onEvent(ProfileEvent.OnUsernameChanged(it)) },
            label = { Text("User Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = textFieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.editEmail,
            onValueChange = { onEvent(ProfileEvent.OnEmailChanged(it)) },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = textFieldColors,
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.editEnterPassword,
            onValueChange = { onEvent(ProfileEvent.OnEnterPasswordChanged(it)) },
            label = { Text("Current Password (if changing)") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = textFieldColors,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.editNewPassword,
            onValueChange = { onEvent(ProfileEvent.OnNewPasswordChanged(it)) },
            label = { Text("New Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = textFieldColors,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        OutlinedTextField(
            value = uiState.editConfirmPassword,
            onValueChange = { onEvent(ProfileEvent.OnConfirmPasswordChanged(it)) },
            label = { Text("Confirm New Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = textFieldColors,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onEvent(ProfileEvent.AttemptUpdateProfile) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.buttonColors(
                containerColor = MediumBlue,
                contentColor = Color.White
            ),
            enabled = !uiState.updateInProgress
        ) {
            if (uiState.updateInProgress) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = Color.White,
                    strokeWidth = 2.dp
                )
            } else {
                Text("Update Profile", fontSize = 16.sp)
            }
        }
    }
}