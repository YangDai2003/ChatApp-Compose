package com.yangdai.chatapp.presentation.bottomNavi

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.yangdai.chatapp.R
import com.yangdai.chatapp.presentation.userList.UserListViewModel
import com.yangdai.chatapp.core.SnackbarController

@Composable
fun BottomNavigation(
    navController: NavController,
    bottomBarState: Boolean,
    snackbarHostState: SnackbarHostState,
) {
    val items = listOf(
        NavItem.UserList,
        NavItem.Profile
    )
    val labels = listOf(
        stringResource(id = R.string.contacts),
        stringResource(id = R.string.account)
    )
    var selectedItem by rememberSaveable { mutableIntStateOf(0) }
    val userListViewModel: UserListViewModel = hiltViewModel()

    val toastMessage = stringResource(id = userListViewModel.toastMessage.value.getStringResource())
    LaunchedEffect(key1 = toastMessage) {
        if (toastMessage.trim().isNotEmpty()) {
            SnackbarController(this).showSnackbar(snackbarHostState, toastMessage, "Close")
        }
    }
    AnimatedVisibility(
        visible = bottomBarState,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
    ) {
        NavigationBar {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination
            items.forEachIndexed { index, item ->
                NavigationBarItem(
                    selected = currentDestination?.hierarchy?.any { it.route == item.screenRoute } == true,
                    onClick = {
                        selectedItem = index
                        navController.navigate(item.screenRoute) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            // Avoid multiple copies of the same destination when selecting the same item
                            launchSingleTop = true
                            // Restore state when selecting a previously selected item
                            restoreState = true
                        }
                    },
                    label = { Text(text = labels[index]) },
                    icon = {
                        if (selectedItem == index) {
                            // 选中时的图标
                            Icon(
                                if (index == 0) {
                                    painterResource(id = R.drawable.chat_fill)
                                } else {
                                    painterResource(id = R.drawable.account_fill)
                                },
                                contentDescription = item.title,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            // 非选中时的图标
                            Icon(
                                if (index == 0) {
                                    painterResource(id = R.drawable.chat_out)
                                } else {
                                    painterResource(id = R.drawable.account_out)
                                },
                                contentDescription = item.title,
                            )
                        }
                    }
                )
            }
        }
    }
}
