package com.yangdai.chatapp.presentation.userList

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yangdai.chatapp.core.Constants
import com.yangdai.chatapp.domain.model.FriendListRegister
import com.yangdai.chatapp.domain.model.FriendListRow
import com.yangdai.chatapp.domain.model.FriendStatus
import com.yangdai.chatapp.domain.usecase.userListScreen.UserListScreenUseCases
import com.yangdai.chatapp.core.SnackbarMsgEnum
import com.yangdai.chatapp.core.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserListViewModel @Inject constructor(
    private val userListScreenUseCases: UserListScreenUseCases
) : ViewModel() {
    var pendingFriendRequestList = mutableStateOf<List<FriendListRegister>>(listOf())
        private set

    var acceptedFriendRequestList = mutableStateOf<List<FriendListRow>>(listOf())
        private set

    var isRefreshing = mutableStateOf(false)
        private set

    var toastMessage = mutableStateOf(SnackbarMsgEnum.NONE)
        private set

    private val _isFirstTime = mutableStateOf(true)
    val isFirstTime get() = _isFirstTime.value

    // 设置 isFirstTime 的方法
    fun setIsFirstTime(value: Boolean) {
        _isFirstTime.value = value
    }

    fun refreshingFriendList() {
        viewModelScope.launch {
            isRefreshing.value = true
            loadPendingFriendRequestListFromFirebase()
            loadAcceptFriendRequestListFromFirebase()
            delay(1000)
            isRefreshing.value = false
        }
    }

    fun createFriendshipRegisterToFirebase(acceptorEmail: String) {
        //Search User -> Check Chat Room -> Create Chat Room -> Check FriendListRegister -> Create FriendListRegister
        viewModelScope.launch {
            userListScreenUseCases.searchUserFromFirebase.invoke(acceptorEmail)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            toastMessage.value = SnackbarMsgEnum.NONE
                        }

                        is Response.Success -> {
                            if (response.data != null) {
                                checkChatRoomExistFromFirebaseAndCreateIfNot(
                                    acceptorEmail,
                                    response.data.profileUUID,
                                    response.data.oneSignalUserId
                                )
                            }
                        }

                        is Response.Error -> {

                        }
                    }

                }
        }
    }

    fun acceptPendingFriendRequestToFirebase(registerUUID: String) {
        viewModelScope.launch {
            userListScreenUseCases.acceptPendingFriendRequestToFirebase.invoke(registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            toastMessage.value = SnackbarMsgEnum.NONE
                        }

                        is Response.Success -> {
                            toastMessage.value = SnackbarMsgEnum.ACCEPT
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    fun cancelPendingFriendRequestToFirebase(registerUUID: String) {
        viewModelScope.launch {
            userListScreenUseCases.rejectPendingFriendRequestToFirebase.invoke(registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            toastMessage.value = SnackbarMsgEnum.NONE
                        }

                        is Response.Success -> {
                            toastMessage.value = SnackbarMsgEnum.CANCEL
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun loadAcceptFriendRequestListFromFirebase() {
        viewModelScope.launch {
            userListScreenUseCases.loadAcceptedFriendRequestListFromFirebase.invoke()
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            if (response.data.isNotEmpty()) {
                                acceptedFriendRequestList.value = response.data
                            }
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun loadPendingFriendRequestListFromFirebase() {
        viewModelScope.launch {
            userListScreenUseCases.loadPendingFriendRequestListFromFirebase.invoke()
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            pendingFriendRequestList.value = response.data
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun checkChatRoomExistFromFirebaseAndCreateIfNot(
        acceptorEmail: String,
        acceptorUUID: String,
        acceptorSignalUserId: String
    ) {
        viewModelScope.launch {
            userListScreenUseCases.checkChatRoomExistedFromFirebase.invoke(acceptorUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            if (response.data == Constants.NO_CHATROOM_IN_FIREBASE_DATABASE) {
                                createChatRoomToFirebase(
                                    acceptorEmail,
                                    acceptorUUID,
                                    acceptorSignalUserId
                                )
                            } else {
                                checkFriendListRegisterIsExistFromFirebase(
                                    response.data,
                                    acceptorEmail,
                                    acceptorUUID,
                                    acceptorSignalUserId
                                )
                            }
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun createChatRoomToFirebase(
        acceptorEmail: String,
        acceptorUUID: String,
        acceptorOneSignalUserId: String
    ) {
        viewModelScope.launch {
            userListScreenUseCases.createChatRoomToFirebase.invoke(acceptorUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {}
                        is Response.Success -> {
                            //Chat Room Created.
                            checkFriendListRegisterIsExistFromFirebase(
                                response.data,
                                acceptorEmail,
                                acceptorUUID,
                                acceptorOneSignalUserId
                            )
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }

    private fun checkFriendListRegisterIsExistFromFirebase(
        chatRoomUUID: String,
        acceptorEmail: String,
        acceptorUUID: String,
        acceptorOneSignalUserId: String
    ) {
        viewModelScope.launch {
            userListScreenUseCases.checkFriendListRegisterIsExistedFromFirebase.invoke(
                acceptorEmail,
                acceptorUUID
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        toastMessage.value = SnackbarMsgEnum.NONE
                    }

                    is Response.Success -> {
                        if (response.data == FriendListRegister()) {
                            toastMessage.value = SnackbarMsgEnum.SEND
                            createFriendListRegisterToFirebase(
                                chatRoomUUID,
                                acceptorEmail,
                                acceptorUUID,
                                acceptorOneSignalUserId
                            )
                        } else if (response.data.status == FriendStatus.PENDING.toString()) {
                            toastMessage.value = SnackbarMsgEnum.REQUESTED
                        } else if (response.data.status == FriendStatus.ACCEPTED.toString()) {
                            toastMessage.value = SnackbarMsgEnum.FRIEND
                        } else if (response.data.status == FriendStatus.BLOCKED.toString()) {
                            openBlockedFriendToFirebase(response.data.registerUUID)
                        }
                    }

                    is Response.Error -> {}
                }
            }
        }
    }

    private fun createFriendListRegisterToFirebase(
        chatRoomUUID: String,
        acceptorEmail: String,
        acceptorUUID: String,
        acceptorOneSignalUserId: String
    ) {
        viewModelScope.launch {
            userListScreenUseCases.createFriendListRegisterToFirebase.invoke(
                chatRoomUUID,
                acceptorEmail,
                acceptorUUID,
                acceptorOneSignalUserId
            ).collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {}
                    is Response.Error -> {}
                }
            }
        }
    }

    private fun openBlockedFriendToFirebase(registerUUID: String) {
        viewModelScope.launch {
            userListScreenUseCases.openBlockedFriendToFirebase.invoke(registerUUID)
                .collect { response ->
                    when (response) {
                        is Response.Loading -> {
                            toastMessage.value = SnackbarMsgEnum.NONE
                        }

                        is Response.Success -> {
                            if (response.data) {
                                toastMessage.value = SnackbarMsgEnum.UNBLOCK
                            } else {
                                toastMessage.value = SnackbarMsgEnum.BLOCKED
                            }
                        }

                        is Response.Error -> {}
                    }
                }
        }
    }
}