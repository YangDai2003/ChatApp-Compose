package com.yangdai.chatapp.presentation.auth

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yangdai.chatapp.domain.model.User
import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.domain.usecase.authScreen.AuthUseCases
import com.yangdai.chatapp.domain.usecase.profileScreen.ProfileScreenUseCases
import com.yangdai.chatapp.core.SnackbarMsgEnum
import com.yangdai.chatapp.core.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authUseCases: AuthUseCases,
    private val profileScreenUseCases: ProfileScreenUseCases
) : ViewModel() {
    var isUserAuthenticatedState = mutableStateOf(false)
        private set

    var isUserSignInState = mutableStateOf(false)
        private set

    var isUserSignUpState = mutableStateOf(false)
        private set

    var toastMessage = mutableStateOf(SnackbarMsgEnum.NONE)
        private set

    init {
        isUserAuthenticated()
    }

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            authUseCases.signIn(email, password).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        toastMessage.value = SnackbarMsgEnum.NONE
                    }
                    is Response.Success -> {
                        setUserStatusToFirebase(UserStatus.ONLINE)
                        isUserSignInState.value = response.data
                        toastMessage.value = SnackbarMsgEnum.LOGIN_SUCCESS
                    }
                    is Response.Error -> {
                        toastMessage.value = SnackbarMsgEnum.LOGIN_FAIL
                    }
                }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            authUseCases.signUp(email, password).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        toastMessage.value = SnackbarMsgEnum.NONE
                    }
                    is Response.Success -> {
                        isUserSignUpState.value = response.data
                        toastMessage.value = SnackbarMsgEnum.SIGNUP_SUCCESS
                        firstTimeCreateProfileToFirebase()
                    }
                    is Response.Error -> {
                        try {
                            toastMessage.value = SnackbarMsgEnum.SIGNUP_FAIL
                        }catch (e: Exception){
                            Log.e("SignUp", e.toString())
                        }
                    }
                }
            }
        }
    }

    private fun isUserAuthenticated() {
        viewModelScope.launch {
            authUseCases.isUserAuthenticated().collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {
                        isUserAuthenticatedState.value = response.data
                        if (response.data) {
                            setUserStatusToFirebase(UserStatus.ONLINE)
                        }
                    }
                    is Response.Error -> {}
                }
            }
        }
    }

    private fun setUserStatusToFirebase(userStatus: UserStatus) {
        viewModelScope.launch {
            profileScreenUseCases.setUserStatusToFirebase(userStatus).collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {}
                    is Response.Error -> {}
                }
            }
        }
    }

    private fun firstTimeCreateProfileToFirebase() {
        viewModelScope.launch {
            profileScreenUseCases.createOrUpdateProfileToFirebase(User()).collect { response ->
                when (response) {
                    is Response.Loading -> {
                        toastMessage.value = SnackbarMsgEnum.NONE
                    }
                    is Response.Success -> {
                        if (response.data) {
                            toastMessage.value = SnackbarMsgEnum.UPDATE
                        } else {
                            toastMessage.value = SnackbarMsgEnum.SAVED
                        }
                    }
                    is Response.Error -> {
                        toastMessage.value = SnackbarMsgEnum.UPDATE_FAIL
                    }
                }
            }
        }
    }

}