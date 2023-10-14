package com.yangdai.chatapp.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yangdai.chatapp.domain.model.UserStatus
import com.yangdai.chatapp.domain.usecase.profileScreen.ProfileScreenUseCases
import com.yangdai.chatapp.core.Response
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val useCases: ProfileScreenUseCases
) : ViewModel() {
    fun setUserStatusToFirebase(userStatus: UserStatus) {
        viewModelScope.launch {
            useCases.setUserStatusToFirebase(userStatus).collect { response ->
                when (response) {
                    is Response.Loading -> {}
                    is Response.Success -> {}
                    is Response.Error -> {}
                }
            }
        }
    }
}