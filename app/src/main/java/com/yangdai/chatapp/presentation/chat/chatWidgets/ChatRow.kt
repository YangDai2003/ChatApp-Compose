package com.yangdai.chatapp.presentation.chat.chatWidgets

import android.icu.text.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yangdai.chatapp.R
import com.yangdai.chatapp.domain.model.MessageRegister
import com.yangdai.chatapp.domain.model.MessageStatus
import java.util.Locale
import java.util.concurrent.TimeUnit

@Composable
fun ChatRow(
    msg: Message
) {
    val isUser = !msg.messageRegister.isMessageFromOpponent
    val arrangement = if (isUser) Arrangement.End else Arrangement.Start

    Column(
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Row(Modifier.fillMaxWidth(), horizontalArrangement = arrangement) {
            TimeAndStatusStamp(msg = msg, isUserMe = !msg.messageRegister.isMessageFromOpponent)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = arrangement) {
            ChatItemBubble(msg, !msg.messageRegister.isMessageFromOpponent)
        }
    }
}

@Composable
private fun TimeAndStatusStamp(msg: Message, isUserMe: Boolean) {
    val currentTimeMillis = System.currentTimeMillis()
    val messageTimeMillis = msg.messageRegister.chatMessage.date // 请将此处替换为实际的日期时间

    val diffMillis = currentTimeMillis - messageTimeMillis
    val hoursAgo = TimeUnit.MILLISECONDS.toHours(diffMillis)

    val formattedDate = when {
        messageTimeMillis == 0L -> ""
        hoursAgo < 24 -> DateFormat.getTimeInstance(DateFormat.SHORT, Locale.getDefault())
            .format(messageTimeMillis)

        hoursAgo < 48 -> stringResource(id = R.string.yesterday)
        else -> DateFormat.getDateInstance(DateFormat.SHORT, Locale.getDefault())
            .format(messageTimeMillis)
    }

    Row {
        if (isUserMe) {
            Icon(
                modifier = Modifier
                    .size(18.dp),
                imageVector = Icons.Default.DoneAll,
                tint = if (msg.messageRegister.chatMessage.status == MessageStatus.READ.toString()) Color.Blue
                else Color.Gray,
                contentDescription = "messageStatus"
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Text(
            text = formattedDate,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

private val roundedCornerShape = RoundedCornerShape(4.dp, 20.dp, 20.dp, 20.dp)
private val roundedCornerShapeFromUser = RoundedCornerShape(20.dp, 20.dp, 4.dp, 20.dp)

@Composable
fun ChatItemBubble(
    message: Message,
    isUserMe: Boolean
) {

    val backgroundBubbleColor = if (isUserMe) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Column {

        Surface(
            color = backgroundBubbleColor,
            shape = if (isUserMe) roundedCornerShapeFromUser else roundedCornerShape
        ) {
            Text(
                text = message.messageRegister.chatMessage.message,
                style = MaterialTheme.typography.bodyLarge.copy(color = LocalContentColor.current),
                modifier = Modifier.padding(16.dp)
            )
        }

        message.image?.let {
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                color = backgroundBubbleColor,
                shape = if (isUserMe) roundedCornerShapeFromUser else roundedCornerShape
            ) {
                Image(
                    painter = painterResource(it),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(160.dp),
                    contentDescription = ""
                )
            }
        }
    }
}

@Immutable
data class Message(
    val messageRegister: MessageRegister,
    val image: Int? = null
)