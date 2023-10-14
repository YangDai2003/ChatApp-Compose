package com.yangdai.chatapp.domain.usecase.chatScreen

import com.yangdai.chatapp.domain.repository.ChatScreenRepository

class LoadMessageFromFirebase(
    private val chatScreenRepository: ChatScreenRepository
) {
    suspend operator fun invoke(
        chatRoomUUID: String,
        opponentUUID: String,
        registerUUID: String
    ) = chatScreenRepository.loadMessagesFromFirebase(chatRoomUUID, opponentUUID, registerUUID)
}