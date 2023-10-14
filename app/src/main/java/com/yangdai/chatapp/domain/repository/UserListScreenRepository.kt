package com.yangdai.chatapp.domain.repository

import com.yangdai.chatapp.domain.model.FriendListRegister
import com.yangdai.chatapp.domain.model.FriendListRow
import com.yangdai.chatapp.domain.model.User
import com.yangdai.chatapp.core.Response
import kotlinx.coroutines.flow.Flow

interface UserListScreenRepository {
    suspend fun loadAcceptedFriendRequestListFromFirebase(): Flow<Response<List<FriendListRow>>>
    suspend fun loadPendingFriendRequestListFromFirebase(): Flow<Response<List<FriendListRegister>>>

    suspend fun searchUserFromFirebase(userEmail: String): Flow<Response<User?>>

    suspend fun checkChatRoomExistedFromFirebase(acceptorUUID: String): Flow<Response<String>>
    suspend fun createChatRoomToFirebase(acceptorUUID: String): Flow<Response<String>>

    suspend fun checkFriendListRegisterIsExistedFromFirebase(
        acceptorEmail: String,
        acceptorUUID: String
    ): Flow<Response<FriendListRegister>>

    suspend fun createFriendListRegisterToFirebase(
        chatRoomUUID: String,
        acceptorEmail: String,
        acceptorUUID: String,
        acceptorOneSignalUserId: String
    ): Flow<Response<Boolean>>

    suspend fun acceptPendingFriendRequestToFirebase(registerUUID: String): Flow<Response<Boolean>>
    suspend fun rejectPendingFriendRequestToFirebase(registerUUID: String): Flow<Response<Boolean>>
    suspend fun openBlockedFriendToFirebase(registerUUID: String): Flow<Response<Boolean>>
}