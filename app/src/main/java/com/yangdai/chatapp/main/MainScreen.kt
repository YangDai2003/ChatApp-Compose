package com.yangdai.chatapp.main

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.yangdai.chatapp.presentation.bottomNavi.NavItem
import com.yangdai.chatapp.presentation.bottomNavi.BottomNavigation
import com.yangdai.chatapp.presentation.bottomNavi.NavGraph
import com.yangdai.chatapp.core.ChatSnackBar

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreenView() {
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val bottomBarState = rememberSaveable { (mutableStateOf(false)) }
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.navigationBarsPadding(),
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                ChatSnackBar(
                    snackbarData = data
                )
            }
        },
        bottomBar = {
            bottomBarState.value =
                currentRoute != NavItem.SignIn.fullRoute &&
                        currentRoute != NavItem.SignUp.fullRoute &&
                        currentRoute != NavItem.Chat.fullRoute
            BottomNavigation(
                navController = navController,
                bottomBarState = bottomBarState.value,
                snackbarHostState
            )
        },
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph(
                navController = navController,
                snackbarHostState = snackbarHostState,
                keyboardController = keyboardController!!
            )
        }
    }
}