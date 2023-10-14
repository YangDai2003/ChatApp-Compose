package com.yangdai.chatapp.domain.usecase.userListScreen

import com.yangdai.chatapp.domain.repository.UserListScreenRepository

class LoadPendingFriendRequestListFromFirebase(
    private val userListScreenRepository: UserListScreenRepository
) {
    suspend operator fun invoke() =
        userListScreenRepository.loadPendingFriendRequestListFromFirebase()
}