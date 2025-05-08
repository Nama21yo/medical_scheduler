package com.example.medical_schedule_app.ui.receptionist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*
import android.app.DatePickerDialog
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color(0xFF374151),
            modifier = Modifier.semantics { contentDescription = "$label label" }
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .semantics { contentDescription = "$label input" },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF374151),
                unfocusedIndicatorColor = Color(0xFF9CA3AF),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorIndicatorColor = MaterialTheme.colorScheme.error
            ),
            isError = isError
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@Composable
fun DatePickerField(
    label: String,
    value: String,
    onDateSelected: (String) -> Unit,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, selectedYear, selectedMonth, selectedDay ->
                val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                    .format(GregorianCalendar(selectedYear, selectedMonth, selectedDay).time)
                onDateSelected(formattedDate)
            },
            year,
            month,
            day
        ).apply {
            datePicker.maxDate = System.currentTimeMillis() // Prevent future dates
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 16.sp,
            color = Color(0xFF374151),
            modifier = Modifier.semantics { contentDescription = "$label label" }
        )
        OutlinedTextField(
            value = value,
            onValueChange = { /* Read-only, changes via picker */ },
            label = { Text(text = label) },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .semantics { contentDescription = "$label input" },
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.colors(
                focusedIndicatorColor = Color(0xFF374151),
                unfocusedIndicatorColor = Color(0xFF9CA3AF),
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                errorIndicatorColor = MaterialTheme.colorScheme.error
            ),
            isError = isError,
            enabled = false, // Disable direct input
            trailingIcon = {
                IconButton(onClick = { datePickerDialog.show() }) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Select date"
                    )
                }
            }
        )
        if (isError && errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPatientFormScreen(
    navController: NavController,
    viewModel: AddPatientFormViewModel = hiltViewModel(),
    onSuccess: () -> Unit = {} // Callback for navigation or reset
) {
    val state by viewModel.state.collectAsState()

    // Handle success state with navigation/reset
    LaunchedEffect(state.isSuccess) {
        if (state.isSuccess) {
            onSuccess() // This will trigger popBackStack in NavGraph
            viewModel.onEvent(AddPatientFormEvent.ResetSuccessState) // Reset the flag
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBFBFB))
            .semantics { contentDescription = "Add Patient Form" }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .background(Color(0xFFB0BECF))
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Add Patient",
                fontSize = 24.sp,
                color = Color(0xFF374151),
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .semantics { contentDescription = "Add Patient Title" }
            )

            InputField(
                label = "First Name",
                value = state.firstName,
                onValueChange = { viewModel.onEvent(AddPatientFormEvent.OnFirstNameChange(it)) },
                isError = state.fullNameError != null,
                errorMessage = state.fullNameError
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "Last Name",
                value = state.lastName,
                onValueChange = { viewModel.onEvent(AddPatientFormEvent.OnLastNameChange(it)) },
                isError = state.fullNameError != null,
                errorMessage = state.fullNameError
            )
            Spacer(modifier = Modifier.height(16.dp))

            DatePickerField(
                label = "Date of Birth",
                value = state.dob,
                onDateSelected = { viewModel.onEvent(AddPatientFormEvent.OnDobChange(it)) },
                isError = state.dobError != null,
                errorMessage = state.dobError
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "Email",
                value = state.email,
                onValueChange = { viewModel.onEvent(AddPatientFormEvent.OnEmailChange(it)) },
                isError = state.emailError != null,
                errorMessage = state.emailError
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "Address",
                value = state.address,
                onValueChange = { viewModel.onEvent(AddPatientFormEvent.OnAddressChange(it)) },
                isError = state.addressError != null,
                errorMessage = state.addressError
            )
            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "Phone Number",
                value = state.phoneNumber,
                onValueChange = { viewModel.onEvent(AddPatientFormEvent.OnPhoneNumberChange(it)) },
                isError = state.phoneNumberError != null,
                errorMessage = state.phoneNumberError
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.onEvent(AddPatientFormEvent.OnSubmitClicked) },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF013A63)),
                modifier = Modifier
                    .width(160.dp)
                    .height(48.dp)
                    .semantics { contentDescription = "Add Patient Button" },
                enabled = !state.isLoading
            ) {
                if (state.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(text = "Add patient", color = Color.White, fontSize = 16.sp)
                }
            }

            // Display general error message if present

            // Display success message temporarily
            if (state.isSuccess) {
                Text(
                    text = "Patient added successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .semantics { contentDescription = "Success Message" }
                )
            }
        }
    }
}