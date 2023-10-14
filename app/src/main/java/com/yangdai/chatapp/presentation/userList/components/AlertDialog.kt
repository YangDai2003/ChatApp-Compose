package com.yangdai.chatapp.presentation.userList.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.yangdai.chatapp.R

@Composable
fun AlertDialogChat(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var emailInput by remember {
        mutableStateOf("")
    }
    AlertDialog(
        icon = {
            Icon(imageVector = Icons.Filled.Person, contentDescription = null)
        },
        title = {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.new_chat),
                textAlign = TextAlign.Center
            )
        },

        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = android.R.string.cancel))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(emailInput)
                }
            ) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },

        text = {
            AlertDialogCustomOutlinedTextField(
                entry = emailInput,
                hint = stringResource(id = R.string.email),
                onChange = { emailInput = it })
        }
    )
}