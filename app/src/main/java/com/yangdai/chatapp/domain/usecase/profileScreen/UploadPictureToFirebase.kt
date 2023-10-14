package com.yangdai.chatapp.domain.usecase.profileScreen

import android.net.Uri
import com.yangdai.chatapp.domain.repository.ProfileScreenRepository

class UploadPictureToFirebase(
    private val profileScreenRepository: ProfileScreenRepository
) {
    suspend operator fun invoke(url: Uri) = profileScreenRepository.uploadPictureToFirebase(url)
}