package com.yangdai.chatapp.core

import androidx.compose.material3.*
import androidx.compose.runtime.Composable

@Composable
fun ChatSnackBar(
    snackbarData: SnackbarData,
    actionOnNewLine: Boolean = false,
) {
    val actionLabel = snackbarData.visuals.actionLabel
    val actionComposable: (@Composable () -> Unit)? = if (actionLabel != null) {
        @Composable {
            TextButton(
                onClick = { snackbarData.performAction() },
                content = { Text(actionLabel) }
            )
        }
    } else {
        null
    }
    Snackbar(
        shape = MaterialTheme.shapes.medium,
        content = { Text(snackbarData.visuals.message) },
        action = actionComposable,
        actionOnNewLine = actionOnNewLine,
    )
}