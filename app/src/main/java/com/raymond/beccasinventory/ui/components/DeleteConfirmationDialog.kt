package com.raymond.beccasinventory.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun DeleteConfirmationDialog(
    itemCount: Int,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    if (itemCount > 0) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = {
                Text(text = "Confirm Deletion")
            },
            text = {
                val message = if (itemCount == 1) {
                    "Are you sure you want to delete this entry? This cannot be undone."
                } else {
                    "Are you sure you want to delete these $itemCount entries? This cannot be undone."
                }
                Text(text = message)
            },
            confirmButton = {
                TextButton(onClick = onConfirm) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        )
    }
}

