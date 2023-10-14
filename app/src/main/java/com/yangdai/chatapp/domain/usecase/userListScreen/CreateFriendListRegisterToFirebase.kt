package com.yangdai.chatapp.domain.usecase.userListScreen

import com.yangdai.chatapp.domain.repository.UserListScreenRepository

class CreateFriendListRegisterToFirebase(
    private val userListScreenRepository: UserListScreenRepository
) {
    suspend operator fun invoke(
        chatRoomUUID: String,
        acceptorEmail: String,
        acceptorUUID: String,
        acceptorOneSignalUserId: String
    ) = userListScreenRepository.createFriendListRegisterToFirebase(
        chatRoomUUID,
        acceptorEmail,
        acceptorUUID,
        acceptorOneSignalUserId
    )
}