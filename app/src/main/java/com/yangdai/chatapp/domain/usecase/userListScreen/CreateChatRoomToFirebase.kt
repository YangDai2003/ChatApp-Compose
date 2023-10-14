package com.yangdai.chatapp.domain.usecase.userListScreen

import com.yangdai.chatapp.domain.repository.UserListScreenRepository

class CreateChatRoomToFirebase(
    private val userListScreenRepository: UserListScreenRepository
) {
    suspend operator fun invoke(acceptorUUID: String) =
        userListScreenRepository.createChatRoomToFirebase(acceptorUUID)
}