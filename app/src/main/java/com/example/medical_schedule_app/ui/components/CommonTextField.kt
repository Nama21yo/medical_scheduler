package com.example.medical_schedule_app.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api // Required for OutlinedTextField
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class) // Add OptIn for Experimental APIs
@Composable
fun CommonTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier, // <<--- ADDED MODIFIER PARAMETER
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    placeholder: String? = null, // Optional placeholder
    isError: Boolean = false,    // Optional error state
    singleLine: Boolean = true   // Optional single line
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        placeholder = placeholder?.let { { Text(it) } }, // Display placeholder if provided
        isError = isError,
        singleLine = singleLine,
        keyboardOptions = KeyboardOptions(
            keyboardType = keyboardType,
            imeAction = imeAction
        ),
        modifier = modifier // Apply the passed-in modifier here
        // .fillMaxWidth() // Remove: Let the caller decide this via the passed modifier
        // .padding(vertical = 8.dp) // Remove: Let the caller decide padding
        // Optional: Add colors to match your theme from OutlinedTextFieldDefaults
    )
}