package com.yangdai.chatapp.presentation.userList

import android.app.Activity
import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yangdai.chatapp.presentation.bottomNavi.NavItem
import com.yangdai.chatapp.presentation.userList.components.ContactsList
import com.yangdai.chatapp.presentation.userList.components.RequestList
import com.yangdai.chatapp.core.SnackbarController
import com.yangdai.chatapp.presentation.userList.components.AlertDialogChat
import com.yangdai.chatapp.presentation.userList.components.ListAppBar

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterialApi::class)
@Composable
fun UserListScreen(
    userListViewModel: UserListViewModel = hiltViewModel(),
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController
) {
    val context = LocalContext.current
    BackHandler {
        (context as? Activity)?.finish()
    }

    val toastMessage = stringResource(id = userListViewModel.toastMessage.value.getStringResource())
    LaunchedEffect(key1 = toastMessage) {
        if (toastMessage.trim().isNotEmpty()) {
            SnackbarController(this).showSnackbar(snackbarHostState, toastMessage, "Close")
        }
    }
    var chatRoomUUID: String? by remember { mutableStateOf(null) }
    var opponentUUID: String? by remember { mutableStateOf(null) }
    var oneSignalUserId: String? by remember { mutableStateOf(null) }
    var registerUUID: String? by remember { mutableStateOf(null) }

    if (chatRoomUUID != null && opponentUUID != null && registerUUID != null && oneSignalUserId != null) {
        LaunchedEffect(key1 = Unit) {
            navController.navigate(NavItem.Chat.screenRoute + "/${chatRoomUUID}" + "/${opponentUUID}" + "/${registerUUID}" + "/${oneSignalUserId}")
        }
    }

    if (userListViewModel.isFirstTime) {
        LaunchedEffect(key1 = Unit) {
            userListViewModel.refreshingFriendList()
        }
        userListViewModel.setIsFirstTime(false)
    }

    val acceptedFriendRequestList = userListViewModel.acceptedFriendRequestList
    val pendingFriendRequestList = userListViewModel.pendingFriendRequestList

    val scrollState = rememberLazyGridState()
    val refreshing by userListViewModel.isRefreshing
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val columnCount = if (isPortrait) 1 else 2
    val pullRefreshState =
        rememberPullRefreshState(refreshing, {
            userListViewModel.refreshingFriendList()
        })

    Box(Modifier.pullRefresh(pullRefreshState)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .focusable()
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { keyboardController.hide() })
                }
        ) {
            var showAlertDialog by remember {
                mutableStateOf(false)
            }
            if (showAlertDialog) {
                AlertDialogChat(
                    onDismiss = { showAlertDialog = !showAlertDialog },
                    onConfirm = {
                        showAlertDialog = !showAlertDialog
                        userListViewModel.createFriendshipRegisterToFirebase(it)
                    })
            }

            ListAppBar(
                onAddClick = {
                    showAlertDialog = !showAlertDialog
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(columnCount), // Display two items per row
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 88.dp), // Add padding to the bottom
                state = scrollState,
            ) {
                items(acceptedFriendRequestList.value) { item ->
                    ContactsList(item) {
                        chatRoomUUID = item.chatRoomUUID
                        registerUUID = item.registerUUID
                        opponentUUID = item.userUUID
                        oneSignalUserId = item.oneSignalUserId
                    }
                }
                items(pendingFriendRequestList.value) { item ->
                    RequestList(item, {
                        userListViewModel.acceptPendingFriendRequestToFirebase(item.registerUUID)
                        userListViewModel.refreshingFriendList()
                    }, {
                        userListViewModel.cancelPendingFriendRequestToFirebase(item.registerUUID)
                        userListViewModel.refreshingFriendList()
                    })
                }
            }
        }

        PullRefreshIndicator(
            refreshing,
            pullRefreshState,
            Modifier
                .padding(top = 56.dp)
                .align(Alignment.TopCenter)
        )
    }
}