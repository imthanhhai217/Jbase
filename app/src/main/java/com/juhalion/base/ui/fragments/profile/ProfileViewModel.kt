package com.juhalion.base.ui.fragments.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juhalion.bae.utils.JuExtendFunction.asSharedFlowIn
import com.juhalion.base.models.comment.user.User
import com.juhalion.base.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val application: Application, private val userRepository: UserRepository
) : AndroidViewModel(application) {

    private val _userData = MutableSharedFlow<List<User>>()
    val userData = _userData.asSharedFlow()

    fun getListUser(){
        viewModelScope.launch {
            userRepository.getAllUsers().collectLatest {
                _userData.emit(it)
            }
        }
    }

    fun upsertUser(user: User){
        viewModelScope.launch {
            userRepository.upsertUser(user)
        }
    }
}