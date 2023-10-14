package com.yangdai.chatapp.presentation.userList.components

import android.icu.text.DateFormat
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberAsyncImagePainter
import com.yangdai.chatapp.R
import com.yangdai.chatapp.domain.model.FriendListRow
import com.yangdai.chatapp.domain.model.MessageStatus
import com.yangdai.chatapp.ui.theme.spacing
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun ContactsList(
    item: FriendListRow,
    onclick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(
                start = spacing.small,
                end = spacing.small,
                top = spacing.small
            )
            .clickable {
                onclick()
            },
        shape = CardDefaults.shape,
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxSize()
        ) {
            val (userPicture, emailText, messageText, dateText, unreadIcon) = createRefs()

            val imageUri = item.userPictureUrl

            Surface(
                modifier = Modifier
                    .constrainAs(userPicture) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .padding(start = spacing.small)
                    .size(60.dp)
                    .clip(CircleShape),
                shape = CircleShape
            ) {
                Image(
                    painter = if (imageUri.isNotEmpty()) {
                        rememberAsyncImagePainter(imageUri)
                    } else {
                        rememberVectorPainter(Icons.Filled.Person) // 使用默认的 Person 图标
                    },
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }

            val baseline = createGuidelineFromTop(0.5f)

            Text(
                text = item.userEmail,
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .constrainAs(emailText) {
                        start.linkTo(userPicture.end)
                        end.linkTo(dateText.start)
                        bottom.linkTo(baseline)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = spacing.small)
            )

            Text(
                text = if (item.lastMessage.profileUUID == item.userUUID) {
                    stringResource(id = R.string.last_msg) + item.lastMessage.message
                } else {
                    stringResource(id = R.string.me) + item.lastMessage.message
                },
                fontWeight = FontWeight.ExtraLight,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .constrainAs(messageText) {
                        top.linkTo(baseline)
                        start.linkTo(userPicture.end)
                        end.linkTo(dateText.start)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = spacing.small),
                lineHeight = 16.sp
            )

            val currentTimeMillis = System.currentTimeMillis()
            val messageTimeMillis = item.lastMessage.date // 请将此处替换为实际的日期时间

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


            Text(
                text = formattedDate,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Light,
                modifier = Modifier
                    .constrainAs(dateText) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }
                    .padding(top = spacing.small, end = spacing.small)
            )

            if (item.lastMessage.status == MessageStatus.RECEIVED.toString() && item.lastMessage.profileUUID == item.userUUID) {
                Icon(
                    imageVector = Icons.Filled.MarkEmailUnread,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier
                        .padding(
                            bottom = spacing.small,
                            end = spacing.small
                        )
                        .constrainAs(unreadIcon) {
                            bottom.linkTo(parent.bottom)
                            end.linkTo(parent.end)
                        }
                )
            }
        }
    }
}

