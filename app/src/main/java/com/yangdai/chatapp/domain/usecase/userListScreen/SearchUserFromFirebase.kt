package com.yangdai.chatapp.domain.usecase.userListScreen

import com.yangdai.chatapp.domain.repository.UserListScreenRepository

class SearchUserFromFirebase(
    private val userListScreenRepository: UserListScreenRepository
) {
    suspend operator fun invoke(userEmail: String) =
        userListScreenRepository.searchUserFromFirebase(userEmail)
}