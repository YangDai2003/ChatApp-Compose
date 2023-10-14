package com.yangdai.chatapp.presentation.chat

import android.annotation.SuppressLint
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.yangdai.chatapp.R
import com.yangdai.chatapp.core.SnackbarController
import com.yangdai.chatapp.domain.model.MessageRegister
import com.yangdai.chatapp.presentation.bottomNavi.NavItem
import com.yangdai.chatapp.presentation.chat.chatWidgets.Message
import com.yangdai.chatapp.presentation.chat.chatWidgets.ChatRow
import com.yangdai.chatapp.presentation.chat.chatWidgets.ChatAppBar
import com.yangdai.chatapp.presentation.chat.chatWidgets.ProfilePictureDialog
import com.yangdai.chatapp.presentation.chat.chatWidgets.UserInput
import com.yangdai.chatapp.presentation.chat.chatWidgets.JumpToBottom
import kotlinx.coroutines.launch
import java.util.*


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreen(
    chatRoomUUID: String,
    opponentUUID: String,
    registerUUID: String,
    oneSignalUserId: String,
    chatViewModel: ChatScreenViewModel = hiltViewModel(),
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController
) {
    val toastMessage = stringResource(id = chatViewModel.toastMessage.value.getStringResource())
    LaunchedEffect(key1 = toastMessage) {
        if (toastMessage.trim().isNotEmpty()) {
            SnackbarController(this).showSnackbar(snackbarHostState, toastMessage, "Close")
        }
    }

    chatViewModel.loadMessagesFromFirebase(chatRoomUUID, opponentUUID, registerUUID)
    ChatScreenContent(
        chatRoomUUID,
        opponentUUID,
        registerUUID,
        oneSignalUserId,
        chatViewModel,
        navController,
        keyboardController
    )

}


@SuppressLint("FrequentlyChangedStateReadInComposition")
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatScreenContent(
    chatRoomUUID: String,
    opponentUUID: String,
    registerUUID: String,
    oneSignalUserId: String,
    chatViewModel: ChatScreenViewModel,
    navController: NavHostController,
    keyboardController: SoftwareKeyboardController
) {
    val messages = chatViewModel.messages

    LaunchedEffect(key1 = Unit) {
        chatViewModel.loadOpponentProfileFromFirebase(opponentUUID)
    }

    val opponentProfileFromFirebase by chatViewModel.opponentProfileFromFirebase

    val opponentName = opponentProfileFromFirebase.userName
    val opponentSurname = opponentProfileFromFirebase.userSurName
    val opponentPictureUrl = opponentProfileFromFirebase.userProfilePictureUrl
    val opponentBio = opponentProfileFromFirebase.userBio
    val opponentStatus = opponentProfileFromFirebase.status

    var showDialog by remember {
        mutableStateOf(false)
    }
    if (showDialog) {
        ProfilePictureDialog(opponentPictureUrl, opponentBio) {
            showDialog = !showDialog
        }
    }

    val scrollState = rememberLazyListState(initialFirstVisibleItemIndex = messages.size)
    val messagesLoadedFirstTime = chatViewModel.messagesLoadedFirstTime.value
    val messageInserted = chatViewModel.messageInserted.value

    LaunchedEffect(key1 = messagesLoadedFirstTime, messages, messageInserted) {
        if (messages.isNotEmpty()) {
            scrollState.scrollToItem(
                index = messages.size - 1
            )
        }
    }

    val imePaddingValues = PaddingValues()
    val imeBottomPadding = imePaddingValues.calculateBottomPadding().value.toInt()
    LaunchedEffect(key1 = imeBottomPadding) {
        if (messages.isNotEmpty()) {
            scrollState.scrollToItem(
                index = messages.size - 1
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .focusable()
            .imePadding()
            .pointerInput(Unit) {
                detectTapGestures(onTap = { keyboardController.hide() })
            }
    ) {

        ChatAppBar(
            title = "$opponentName $opponentSurname",
            description = if (opponentStatus.lowercase() == "online") {
                stringResource(id = R.string.online)
            } else {
                stringResource(id = R.string.offline)
            },
            pictureUrl = opponentPictureUrl,
            onBackArrowClick = {
                navController.navigateUp()
            },
            onUserProfilePictureClick = {
                showDialog = true
            },
            onMoreDropDownBlockUserClick = {
                chatViewModel.blockFriendToFirebase(registerUUID)
                navController.navigate(NavItem.UserList.fullRoute)
            }
        )

        Box(Modifier.weight(1f)) {
            LazyColumn(
                state = scrollState
            ) {
                items(messages) { message: MessageRegister ->
                    val msg = Message(message, null)
                    ChatRow(msg)
                }
            }

            val scope = rememberCoroutineScope()
            val isAtBottom =
                scrollState.layoutInfo.visibleItemsInfo.any { it.index == messages.size - 1 }
            JumpToBottom(
                // 仅当不在底部时才显示
                enabled = !scrollState.isScrollInProgress && !isAtBottom && messages.isNotEmpty(),
                onClicked = {
                    scope.launch {
                        scrollState.animateScrollToItem(messages.size - 1)
                    }
                },
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }

        UserInput(
            onMessageChange = { messageContent ->
                chatViewModel.insertMessageToFirebase(
                    chatRoomUUID,
                    messageContent,
                    registerUUID,
                    oneSignalUserId
                )
            }
        )
    }
}