package com.yangdai.chatapp.domain.usecase.profileScreen

import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.domain.repository.ProfileScreenRepository

class SetUserStatusToFirebase(
    private val profileScreenRepository: ProfileScreenRepository
) {
    suspend operator fun invoke(userStatus: UserStatus) =
        profileScreenRepository.setUserStatusToFirebase(userStatus)
}