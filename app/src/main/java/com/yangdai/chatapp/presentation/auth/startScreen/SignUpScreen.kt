package com.yangdai.chatapp.presentation.auth.startScreen

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yangdai.chatapp.R
import com.yangdai.chatapp.core.SnackbarController
import com.yangdai.chatapp.presentation.auth.AuthViewModel
import com.yangdai.chatapp.presentation.bottomNavi.NavItem
import com.yangdai.chatapp.presentation.auth.components.BottomSwitchSign
import com.yangdai.chatapp.presentation.auth.components.ButtonSign
import com.yangdai.chatapp.presentation.auth.components.LoginEmailCustomOutlinedTextField
import com.yangdai.chatapp.presentation.auth.components.LoginPasswordCustomOutlinedTextField
import com.yangdai.chatapp.ui.theme.spacing

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SignUpScreen(
    emailFromSignIn: String,
    authViewModel: AuthViewModel = hiltViewModel(),
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController
) {

    //Set SnackBar
    val toastMessage = stringResource(id = authViewModel.toastMessage.value.getStringResource())
    LaunchedEffect(key1 = toastMessage) {
        if (toastMessage.trim().isNotEmpty()) {
            SnackbarController(this).showSnackbar(snackbarHostState, toastMessage, "Close")
        }
    }

    //For test user information
    var textEmail: String? by remember { mutableStateOf("") }
    var textPassword: String? by remember { mutableStateOf("") }
    LaunchedEffect(key1 = Unit) {
        textEmail = emailFromSignIn
    }

    //Sign Up Navigate
    val isUserSignUp = authViewModel.isUserSignUpState.value
    LaunchedEffect(key1 = isUserSignUp) {
        if (isUserSignUp) {
            keyboardController.hide()
            navController.navigate(NavItem.Profile.fullRoute)
        }
    }

    //Compose Components
    Column {
        Surface(
            modifier = Modifier
                .weight(8f)
                .fillMaxSize()
                .focusable(true)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = {
                        keyboardController.hide()
                    })
                }
                .padding(spacing.large)) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(id = R.string.app_name),
                    style = MaterialTheme.typography.titleLarge
                )

                Box(modifier = Modifier.padding(top = spacing.extraLarge)) {
                    LoginEmailCustomOutlinedTextField(
                        textEmail!!,
                        stringResource(id = R.string.email),
                        Icons.Default.Email
                    ) {
                        textEmail = it
                    }
                }
                Box(modifier = Modifier.padding(top = spacing.medium)) {
                    LoginPasswordCustomOutlinedTextField(
                        textPassword!!,
                        stringResource(id = R.string.password),
                        Icons.Default.Password
                    ) {
                        textPassword = it
                    }
                }

                ButtonSign(
                    onclick = {
                        authViewModel.signUp(textEmail!!.trim(), textPassword!!.trim())
                    },
                    signInOrSignUp = stringResource(id = R.string.sign_up)
                )
            }
        }

        BottomSwitchSign(
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
                .focusable(true)
                .pointerInput(Unit) {
                    detectTapGestures(onTap = { keyboardController.hide() })
                },
            onclick = {
                if (textEmail == "") {
                    navController.popBackStack()
                    navController.navigate(NavItem.SignIn.fullRoute)
                } else {
                    navController.popBackStack()
                    navController.navigate(NavItem.SignIn.screenRoute + "?emailFromSignUp=$textEmail")
                }
            },
            signInOrSignUp = stringResource(id = R.string.sign_in),
            label = stringResource(id = R.string.hasAccount)
        )
    }
}