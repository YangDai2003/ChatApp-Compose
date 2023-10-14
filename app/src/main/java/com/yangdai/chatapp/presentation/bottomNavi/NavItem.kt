package com.yangdai.chatapp.presentation.bottomNavi

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    var title: String,
    var icon: ImageVector,
    var screenRoute: String,
    var arguments: String
) {

    // 登录界面
    object SignIn : NavItem(
        "SignIn",
        Icons.Filled.Person,
        "signin",
        "?emailFromSignUp={emailFromSignUp}"
    ) {
        val fullRoute = screenRoute + arguments
    }

    // 注册界面
    object SignUp : NavItem(
        "SignUp",
        Icons.Filled.Person,
        "signup",
        "?emailFromSignIn={emailFromSignIn}"
    ) {
        val fullRoute = screenRoute + arguments
    }

    // 资料界面
    object Profile : NavItem(
        "Profile",
        Icons.Filled.Person,
        "profile",
        ""
    ) {
        val fullRoute = screenRoute + arguments
    }

    // 列表界面
    object UserList : NavItem(
        "Chat",
        Icons.Filled.Chat,
        "userlist",
        ""
    ) {
        val fullRoute = screenRoute + arguments
    }

    // 聊天界面
    object Chat : NavItem(
        "Chat",
        Icons.Filled.Chat,
        "chat",
        "/{chatroomUUID}" + "/{opponentUUID}" + "/{registerUUID}" + "/{oneSignalUserId}"
    ) {
        val fullRoute = screenRoute + arguments
    }

}