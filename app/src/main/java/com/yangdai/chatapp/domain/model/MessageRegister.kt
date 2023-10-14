package com.yangdai.chatapp.domain.model

import androidx.annotation.Keep

@Keep
data class MessageRegister(
    var chatMessage: ChatMessage,
    var isMessageFromOpponent: Boolean
)