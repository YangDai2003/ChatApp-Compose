package com.yangdai.chatapp.domain.model

import androidx.annotation.Keep

@Keep
data class FriendListRow(
    val chatRoomUUID: String,
    val userEmail: String = "",
    val userUUID: String = "",
    val oneSignalUserId: String,
    val registerUUID: String = "",
    val userPictureUrl: String = "",
    val lastMessage: ChatMessage = ChatMessage()
)