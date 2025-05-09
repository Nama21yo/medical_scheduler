package com.example.medical_schedule_app.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.layout.padding // Keep this if you still want internal padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api // Required for OutlinedTextField
//import androidx.compose.material3.OutlinedTextFieldDefaults // For customizing colors if needed
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
//import androidx.compose.ui.graphics.Color // If you want to customize colors
//import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.example.medical_schedule_app.data.models.Role
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleSelector(
    roles: List<Role>,
    selectedRole: Role?,
    onRoleSelected: (Role) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
    ) {
        var parentSize by remember { mutableStateOf(androidx.compose.ui.geometry.Size.Zero) }

        OutlinedTextField(
            value = selectedRole?.name ?: "Select Role",
            onValueChange = {},
            readOnly = true,
            label = { Text("Role") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.ArrowDropDown,
                    contentDescription = "Dropdown Arrow",
                    modifier = Modifier.clickable { expanded = !expanded }
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .onGloballyPositioned {
                    parentSize = it.size.toSize()
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { parentSize.width.toDp() })
        ) {
            if (roles.isEmpty()) {
                DropdownMenuItem(
                    text = { Text("No roles available") },
                    onClick = { expanded = false },
                    enabled = false
                )
            } else {
                roles.forEach { role ->
                    DropdownMenuItem(
                        text = { Text(role.name) },
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
