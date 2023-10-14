package com.yangdai.chatapp.domain.usecase.authScreen

import com.yangdai.chatapp.domain.repository.AuthScreenRepository

class IsUserAuthenticatedInFirebase(
    private val authScreenRepository: AuthScreenRepository
) {
    operator fun invoke() = authScreenRepository.isUserAuthenticatedInFirebase()
}