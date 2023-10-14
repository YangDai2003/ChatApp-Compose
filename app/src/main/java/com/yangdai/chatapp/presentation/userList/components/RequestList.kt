package com.yangdai.chatapp.presentation.userList.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.yangdai.chatapp.R
import com.yangdai.chatapp.domain.model.ChatMessage
import com.yangdai.chatapp.domain.model.FriendListRegister
import com.yangdai.chatapp.ui.theme.spacing


@Composable
fun RequestList(
    item: FriendListRegister,
    onAcceptClick: () -> Unit = {},
    onCancelClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    vertical = spacing.medium,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                modifier = Modifier.padding(spacing.small),
                text = item.requesterEmail,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Row {
                TextButton(
                    onClick = { onCancelClick() }
                ) {
                    Text(text = stringResource(id = R.string.decline), color = MaterialTheme.colorScheme.error)
                }
                TextButton(
                    onClick = { onAcceptClick() }
                ) {
                    Text(text = stringResource(id = R.string.accept))
                }
            }

        }
    }
}

@Preview
@Composable
fun PendingFriendRequestListPreview() {
    val item = FriendListRegister(
        chatRoomUUID = "123456",
        registerUUID = "999999",
        requesterEmail = "gmail",
        requesterUUID = "666666",
        requesterOneSignalUserId = "987654321",
        acceptorEmail = "qq",
        acceptorUUID = "000000",
        acceptorOneSignalUserId = "111111",
        status = "online",
        lastMessage = ChatMessage()
    )
    RequestList(item = item)
}