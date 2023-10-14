package com.yangdai.chatapp.domain.usecase.authScreen

import com.yangdai.chatapp.domain.repository.AuthScreenRepository

class SignIn(
    private val authScreenRepository: AuthScreenRepository
) {
    suspend operator fun invoke(email: String, password: String) =
        authScreenRepository.signIn(email, password)
}