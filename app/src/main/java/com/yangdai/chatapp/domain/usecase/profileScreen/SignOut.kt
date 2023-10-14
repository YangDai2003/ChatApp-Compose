package com.yangdai.chatapp.domain.usecase.profileScreen

import com.yangdai.chatapp.domain.repository.ProfileScreenRepository

class SignOut(
    private val profileScreenRepository: ProfileScreenRepository
) {
    suspend operator fun invoke() = profileScreenRepository.signOut()
}