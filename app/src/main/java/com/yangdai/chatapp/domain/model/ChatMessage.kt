package com.yangdai.chatapp.domain.model

import androidx.annotation.Keep

@Keep
data class ChatMessage(
    val profileUUID: String = "",
    var message: String = "",
    var date: Long = 0,
    var status: String = ""
)
