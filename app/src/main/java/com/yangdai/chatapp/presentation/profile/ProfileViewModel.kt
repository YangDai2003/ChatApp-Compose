package com.yangdai.chatapp.presentation.profile

import android.content.ContentValues
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yangdai.chatapp.domain.model.User
import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.domain.usecase.profileScreen.ProfileScreenUseCases
import com.yangdai.chatapp.core.SnackbarMsgEnum
import com.yangdai.chatapp.core.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val useCases: ProfileScreenUseCases
) : ViewModel() {
    var toastMessage = mutableStateOf(SnackbarMsgEnum.NONE)
        private set

    var isLoading = mutableStateOf(false)
        private set

    var isUserSignOutState = mutableStateOf(false)
        private set

    var userDataStateFromFirebase = mutableStateOf(User())
        private set

    init {
        loadProfileFromFirebase()
    }

    //PUBLIC FUNCTIONS

    fun setUserStatusToFirebaseAndSignOut(userStatus: UserStatus){
        viewModelScope.launch {
            useCases.setUserStatusToFirebase(userStatus).collect{ response ->
                when(response){
                    is Response.Loading -> {}
                    is Response.Success -> {
                        if(response.data){
                            signOut()
                        }
                    }
                    is Response.Error -> {}
                }
            }
        }
    }

    fun uploadPictureToFirebase(uri: Uri) {
        viewModelScope.launch {
            useCases.uploadPictureToFirebase(uri).collect { response ->
                when(response){
                    is Response.Loading -> {
                        isLoading.value = true
                    }
                    is Response.Success -> {
                        //Picture Uploaded
                        isLoading.value = false
                        updateProfileToFirebase(
                            User(userProfilePictureUrl = response.data))
                    }
                    is Response.Error -> {}
                }
            }
        }
    }

    fun updateProfileToFirebase(myUser: User) {
        viewModelScope.launch {
            useCases.createOrUpdateProfileToFirebase(myUser).collect { response ->
                when(response){
                    is Response.Loading -> {
                        toastMessage.value = SnackbarMsgEnum.NONE
                        isLoading.value = true
                    }
                    is Response.Success -> {
                        isLoading.value = false
                        if(response.data){
                            toastMessage.value = SnackbarMsgEnum.UPDATE
                        }else{
                            toastMessage.value = SnackbarMsgEnum.SAVED
                        }
                        loadProfileFromFirebase()
                    }
                    is Response.Error -> {
                        toastMessage.value = SnackbarMsgEnum.UPDATE_FAIL
                    }
                }
            }
        }
    }


    //PRIVATE FUNCTIONS

    private fun signOut() {
        viewModelScope.launch {
            useCases.signOut().collect { response ->
                when(response) {
                    is Response.Loading -> {
                        toastMessage.value = SnackbarMsgEnum.NONE
                    }
                    is Response.Success -> {
                        isUserSignOutState.value = response.data
                        toastMessage.value = SnackbarMsgEnum.SIGNOUT
                    }
                    is Response.Error -> Log.e(ContentValues.TAG, response.message)
                }

            }
        }
    }

    private fun loadProfileFromFirebase() {
        viewModelScope.launch {
            useCases.loadProfileFromFirebase().collect { response ->
                when(response){
                    is Response.Loading -> {
                        isLoading.value = true
                    }
                    is Response.Success -> {
                        userDataStateFromFirebase.value = response.data
                        delay(500)
                        isLoading.value = false
                    }
                    is Response.Error -> {}
                }
            }
        }
    }
}