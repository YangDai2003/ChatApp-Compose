package com.yangdai.chatapp.domain.usecase.profileScreen

import com.yangdai.chatapp.domain.model.User
import com.yangdai.chatapp.domain.repository.ProfileScreenRepository

class CreateOrUpdateProfileToFirebase(
    private val profileScreenRepository: ProfileScreenRepository
) {
    suspend operator fun invoke(user: User) =
        profileScreenRepository.createOrUpdateProfileToFirebase(user)
}