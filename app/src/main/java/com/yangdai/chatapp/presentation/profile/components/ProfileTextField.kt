package com.yangdai.chatapp.presentation.profile.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.yangdai.chatapp.ui.theme.spacing

@Composable
fun ProfileTextField(
    modifier: Modifier = Modifier,
    entry: String,
    hint: String,
    onChange: (String) -> Unit = {},
    keyboardType: KeyboardType = KeyboardType.Text
) {
    var isNameChange by remember {
        mutableStateOf(false)
    }
    var text by remember {
        mutableStateOf("")
    }
    text = entry

    OutlinedTextField(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = spacing.medium),
        label = { Text(text = hint) },
        value = text,
        onValueChange = {
            text = it
            onChange(it)
            isNameChange = true
        },
        singleLine = true,
        maxLines = 1,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
    )
}
