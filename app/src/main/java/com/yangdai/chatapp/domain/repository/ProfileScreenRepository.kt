package com.yangdai.chatapp.domain.repository

import android.net.Uri
import com.yangdai.chatapp.domain.model.User
import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.core.Response
import kotlinx.coroutines.flow.Flow

interface ProfileScreenRepository {
    suspend fun signOut(): Flow<Response<Boolean>>
    suspend fun uploadPictureToFirebase(url: Uri): Flow<Response<String>>
    suspend fun createOrUpdateProfileToFirebase(user: User): Flow<Response<Boolean>>
    suspend fun loadProfileFromFirebase(): Flow<Response<User>>
    suspend fun setUserStatusToFirebase(userStatus: UserStatus): Flow<Response<Boolean>>
}