package com.yangdai.chatapp.domain.usecase.userListScreen

import com.yangdai.chatapp.domain.repository.UserListScreenRepository

class CheckFriendListRegisterIsExistedFromFirebase(
    private val userListScreenRepository: UserListScreenRepository
) {
    suspend operator fun invoke(
        acceptorEmail: String,
        acceptorUUID: String
    ) =
        userListScreenRepository.checkFriendListRegisterIsExistedFromFirebase(
            acceptorEmail,
            acceptorUUID
        )
}