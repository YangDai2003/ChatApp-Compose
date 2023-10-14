package com.yangdai.chatapp.domain.usecase.chatScreen

import com.yangdai.chatapp.domain.repository.ChatScreenRepository

class LoadOpponentProfileFromFirebase(
    private val chatScreenRepository: ChatScreenRepository
) {
    suspend operator fun invoke(opponentUUID: String) =
        chatScreenRepository.loadOpponentProfileFromFirebase(opponentUUID)
}