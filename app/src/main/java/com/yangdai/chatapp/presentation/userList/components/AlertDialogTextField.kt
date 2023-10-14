package com.yangdai.chatapp.presentation.userList.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier

@Composable
fun AlertDialogCustomOutlinedTextField(
    entry: String,
    hint: String,
    onChange: (String) -> Unit = {}
) {

    var isNameChange by remember { mutableStateOf(false) }

    var text by remember { mutableStateOf("") }
    text = entry


    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        singleLine = true,
        value = text,
        label = { Text(hint) },
        onValueChange = {
            text = it
            onChange(it)
            isNameChange = true
        })
}