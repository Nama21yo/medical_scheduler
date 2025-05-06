package com.example.medical_schedule_app.ui.receptionist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
// import androidx.compose.ui.text.input.KeyboardType // Not used in this InputField, can be removed
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 16.sp, color = Color(0xFF374151)) // This is an external label
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(text = label) }, // This is the Material Design label that animates
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                // .background(Color.Transparent) // Consider removing if containerColor handles it
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(0.dp), // Makes the corners sharp
            colors = TextFieldDefaults.colors( // Use TextFieldDefaults.colors()
                // For OutlinedTextField, "indicator" color refers to the border
                focusedIndicatorColor = Color(0xFF374151),    // Color of the border when focused
                unfocusedIndicatorColor = Color(0xFF9CA3AF),  // Color of the border when not focused
                // Container colors for the background of the text field
                focusedContainerColor = Color.Transparent,    // Background when focused
                unfocusedContainerColor = Color.Transparent,  // Background when not focused
                // You can also specify other colors like text color, cursor color, label color etc.
                // For example, if you want to customize the label color:
                // focusedLabelColor = Color(0xFF374151),
                // unfocusedLabelColor = Color(0xFF9CA3AF)
            )
        )
    }
}

@Composable
fun AddPatientFormScreen() {
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEBFBFB))
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
                modifier = Modifier.padding(bottom = 24.dp)
            )

            InputField("Full Name", fullName) { fullName = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Date of Birth", dob) { dob = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Email", email) { email = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Address", address) { address = it }
            Spacer(modifier = Modifier.height(16.dp))
            InputField("Phone Number", phone) { phone = it }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { /* handle submission */ },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF013A63)),
                modifier = Modifier
                    .width(160.dp)
                    .height(48.dp)
            ) {
                Text(text = "Add patient", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddPatientFormScreenPreview() {
    MaterialTheme {
        AddPatientFormScreen()
    }
}