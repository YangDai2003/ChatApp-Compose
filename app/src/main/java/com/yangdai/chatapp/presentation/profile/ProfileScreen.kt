package com.yangdai.chatapp.presentation.profile

import android.app.Activity
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.yangdai.chatapp.R
import com.yangdai.chatapp.core.SnackbarController
import com.yangdai.chatapp.core.SnackbarMsgEnum
import com.yangdai.chatapp.domain.model.User
import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.presentation.bottomNavi.NavItem
import com.yangdai.chatapp.presentation.profile.components.PickPicFromGallery
import com.yangdai.chatapp.presentation.profile.components.ProfileAppBar
import com.yangdai.chatapp.presentation.profile.components.ProfileTextField
import com.yangdai.chatapp.ui.theme.spacing
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navController: NavController,
    snackbarHostState: SnackbarHostState,
    keyboardController: SoftwareKeyboardController
) {
    val context = LocalContext.current
    BackHandler {
        (context as? Activity)?.finish()
    }

    val toastMessage = stringResource(id = profileViewModel.toastMessage.value.getStringResource())
    LaunchedEffect(key1 = toastMessage) {
        if (toastMessage.trim().isNotEmpty()) {
            SnackbarController(this).showSnackbar(snackbarHostState, toastMessage, "Close")
            // 在延迟时间后关闭Snackbar
            launch {
                delay(3000) // 延迟3秒
                profileViewModel.toastMessage.value = SnackbarMsgEnum.NONE
            }
        }
    }

    var isLoading by remember {
        mutableStateOf(false)
    }
    isLoading = profileViewModel.isLoading.value

    var userDataFromFirebase by remember { mutableStateOf(User()) }
    userDataFromFirebase = profileViewModel.userDataStateFromFirebase.value

    var email by remember {
        mutableStateOf("")
    }
    email = userDataFromFirebase.userEmail

    var name by remember { mutableStateOf("") }
    name = userDataFromFirebase.userName

    var surName by remember {
        mutableStateOf("")
    }
    surName = userDataFromFirebase.userSurName

    var bio by remember { mutableStateOf("") }
    bio = userDataFromFirebase.userBio

    var phoneNumber by remember { mutableStateOf("") }
    phoneNumber = userDataFromFirebase.userPhoneNumber

    var userDataPictureUrl by remember { mutableStateOf("") }
    userDataPictureUrl = userDataFromFirebase.userProfilePictureUrl

    val scrollState = rememberScrollState()

    val updatedImage by remember {
        mutableStateOf<Uri?>(null)
    }
    val isUserSignOut = profileViewModel.isUserSignOutState.value
    LaunchedEffect(key1 = isUserSignOut) {
        if (isUserSignOut) {
            navController.popBackStack()
            navController.navigate(NavItem.SignIn.fullRoute)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentHeight()
            .imePadding()
    ) {
        ProfileAppBar()
        if (isLoading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressIndicator()
            }
        } else {
            Surface(
                modifier = Modifier
                    .focusable(true)
                    .pointerInput(Unit) {
                        detectTapGestures(onTap = { keyboardController.hide() })
                    }
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = spacing.medium)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    Box(
                        contentAlignment = Alignment.Center
                    ) {
                        PickPicFromGallery(userDataPictureUrl) {
                            if (it != null) {
                                profileViewModel.uploadPictureToFirebase(it)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(spacing.medium))

                    Text(text = email, style = MaterialTheme.typography.titleMedium)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = spacing.medium),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ProfileTextField(
                            entry = name,
                            hint = stringResource(id = R.string.firstname),
                            onChange = { name = it },
                            modifier = Modifier.weight(1f)
                        )

                        Spacer(modifier = Modifier.width(spacing.medium))

                        ProfileTextField(
                            entry = surName,
                            hint = stringResource(id = R.string.lastname),
                            onChange = { surName = it },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    ProfileTextField(
                        entry = bio,
                        hint = stringResource(id = R.string.about),
                        onChange = { bio = it })

                    ProfileTextField(
                        entry = phoneNumber,
                        hint = stringResource(id = R.string.phone_number),
                        onChange = { phoneNumber = it },
                        keyboardType = KeyboardType.Phone
                    )

                    Button(
                        modifier = Modifier
                            .padding(top = spacing.large)
                            .wrapContentWidth(),
                        onClick = {
                            val user = User(
                                userName = name.ifEmpty { "" },
                                userSurName = surName.ifEmpty { "" },
                                userBio = bio.ifEmpty { "" },
                                userPhoneNumber = phoneNumber.ifEmpty { "" }
                            )
                            profileViewModel.updateProfileToFirebase(user)
                        },
                        enabled = updatedImage != null || name != "" || surName != "" || bio != "" || phoneNumber != ""
                    ) {
                        Text(
                            text = stringResource(id = R.string.save),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Button(
                        modifier = Modifier
                            .wrapContentWidth()
                            .padding(top = spacing.medium, bottom = 88.dp), //导航栏大小加间距
                        onClick = { profileViewModel.setUserStatusToFirebaseAndSignOut(UserStatus.OFFLINE) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text(
                            text = stringResource(id = R.string.log_out),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }

        }
    }
}
