package com.example.medical_schedule_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding // Keep this if you still want internal padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api // Required for OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults // For customizing colors if needed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color // If you want to customize colors
import androidx.compose.ui.unit.dp
import com.example.medical_schedule_app.data.models.Role

@OptIn(ExperimentalMaterial3Api::class) // Add OptIn for Experimental APIs
@Composable
fun RoleSelector(
    roles: List<Role>,
    selectedRole: Role?,
    onRoleSelected: (Role) -> Unit,
    modifier: Modifier = Modifier // <<--- ADDED MODIFIER PARAMETER WITH DEFAULT
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier // Apply the passed-in modifier here
        // .fillMaxWidth() // Remove this if you want the caller to control width
        // .padding(vertical = 8.dp) // Keep or remove based on whether this padding is intrinsic or extrinsic
    ) {
        OutlinedTextField(
            value = selectedRole?.name ?: "Select Role",
            onValueChange = {}, // It's read-only, action is on click
            readOnly = true,
            label = { Text("Role") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.clickable { expanded = !expanded } // Toggle on icon click too
                )
            },
            modifier = Modifier // This modifier is for the OutlinedTextField itself
                .fillMaxWidth() // Makes the TextField fill the width of the Box
                .clickable { expanded = !expanded }, // Make the whole field clickable to expand
            // Optional: Add colors to match your theme
            // colors = OutlinedTextFieldDefaults.colors(
            //     focusedBorderColor = YourAppTheme.colors.primary, // Example
            //     unfocusedBorderColor = YourAppTheme.colors.onSurface.copy(alpha = 0.3f) // Example
            // )
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth() // Make dropdown menu itself take available width
            // Or use .wrapContentWidth() if you want it to size to content
            // and align it (e.g. Modifier.align(Alignment.CenterStart))
        ) {
            if (roles.isEmpty()) {
                DropdownMenuItem(
                    text = { Text(text = "No roles available") },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(text = role.name) },
                        onClick = {
                            onRoleSelected(role)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}