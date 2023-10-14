package com.yangdai.chatapp.presentation.auth.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.yangdai.chatapp.ui.theme.spacing


@Composable
fun ButtonSign(
    onclick: () -> Unit,
    signInOrSignUp: String
) {
    Button(
    modifier = Modifier
    .padding(top = spacing.large),
    onClick = {
        onclick()
    })
    {
        Text(
            text = signInOrSignUp,
            style = MaterialTheme.typography.titleMedium
        )
    }
}