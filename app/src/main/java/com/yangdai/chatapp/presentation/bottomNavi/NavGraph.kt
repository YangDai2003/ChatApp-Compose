package com.yangdai.chatapp.presentation.bottomNavi

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.yangdai.chatapp.presentation.auth.startScreen.SignInScreen
import com.yangdai.chatapp.presentation.auth.startScreen.SignUpScreen
import com.yangdai.chatapp.presentation.chat.ChatScreen
import com.yangdai.chatapp.presentation.profile.ProfileScreen
import com.yangdai.chatapp.presentation.userList.UserListScreen

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController
) {
    NavHost(
        navController = navController,
        startDestination = NavItem.SignIn.fullRoute
    ) {

        //SIGN IN SCREEN
        composable(
            route = NavItem.SignIn.fullRoute,
            exitTransition = {
                ExitTransition.None
            },
            enterTransition = {
                EnterTransition.None
            },
            arguments = listOf(
                navArgument("emailFromSignUp") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val emailFromSignUp = remember {
                it.arguments?.getString("emailFromSignUp")
            }

            SignInScreen(
                emailFromSignUp = emailFromSignUp ?: "",
                navController = navController,
                snackbarHostState = snackbarHostState,
                keyboardController = keyboardController
            )
        }

        composable(
            route = NavItem.SignUp.fullRoute,
            exitTransition = {
                ExitTransition.None
            },
            enterTransition = {
                EnterTransition.None
            },
            arguments = listOf(
                navArgument("emailFromSignIn") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) {
            val emailFromSignIn = remember {
                it.arguments?.getString("emailFromSignIn")
            }
            SignUpScreen(
                emailFromSignIn = emailFromSignIn ?: "",
                navController = navController,
                snackbarHostState = snackbarHostState,
                keyboardController = keyboardController
            )
        }

        composable(
            route = NavItem.Profile.fullRoute,
            exitTransition = {
                fadeOut()
            },
            enterTransition = {
                fadeIn()
            }
        ) {
            ProfileScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                keyboardController = keyboardController
            )
        }

        composable(
            route = NavItem.UserList.fullRoute,
            exitTransition = {
                fadeOut()
            },
            enterTransition = {
                fadeIn()
            },
        ) {
            UserListScreen(
                navController = navController,
                snackbarHostState = snackbarHostState,
                keyboardController = keyboardController
            )
        }

        composable(
            route = NavItem.Chat.fullRoute,
            exitTransition = {
                fadeOut()
            },
            enterTransition = {
                expandVertically()
            },
            arguments = listOf(
                navArgument("chatroomUUID") {
                    type = NavType.StringType
                }, navArgument("opponentUUID") {
                    type = NavType.StringType
                }, navArgument("registerUUID") {
                    type = NavType.StringType
                }, navArgument("oneSignalUserId") {
                    type = NavType.StringType
                })
        ) {

            val chatroomUUID = remember {
                it.arguments?.getString("chatroomUUID")
            }
            val opponentUUID = remember {
                it.arguments?.getString("opponentUUID")
            }
            val registerUUID = remember {
                it.arguments?.getString("registerUUID")
            }
            val oneSignalUserId = remember {
                it.arguments?.getString("oneSignalUserId")
            }

            ChatScreen(
                chatRoomUUID = chatroomUUID ?: "",
                opponentUUID = opponentUUID ?: "",
                registerUUID = registerUUID ?: "",
                oneSignalUserId = oneSignalUserId ?: "",
                navController = navController,
                snackbarHostState = snackbarHostState,
                keyboardController = keyboardController
            )
        }
    }
}
