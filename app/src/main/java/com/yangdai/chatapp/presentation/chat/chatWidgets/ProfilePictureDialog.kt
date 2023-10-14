package com.yangdai.chatapp.presentation.chat.chatWidgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.rememberAsyncImagePainter
import com.yangdai.chatapp.ui.theme.spacing

@Composable
fun ProfilePictureDialog(
    profilePictureUrl: String,
    profileBio: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties()
    ) {
        Card {
            Image(
                painter = if (profilePictureUrl.isNotEmpty()) {
                    rememberAsyncImagePainter(profilePictureUrl)
                } else {
                    rememberVectorPainter(Icons.Filled.Person) // 使用默认的 Person 图标
                },
                contentDescription = null,
                modifier = Modifier
                    .padding(spacing.large)
                    .size(250.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Text(
                profileBio,
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
    }
}