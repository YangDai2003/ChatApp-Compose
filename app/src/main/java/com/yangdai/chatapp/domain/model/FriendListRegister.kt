package com.yangdai.chatapp.domain.model

import androidx.annotation.Keep

@Keep
data class FriendListRegister(
    var chatRoomUUID: String = "",
    var registerUUID: String = "",
    var requesterEmail: String = "",
    var requesterUUID: String = "",
    var requesterOneSignalUserId: String = "",
    var acceptorEmail: String = "",
    var acceptorUUID: String = "",
    var acceptorOneSignalUserId: String = "",
    var status: String = "",
    var lastMessage: ChatMessage = ChatMessage()
)