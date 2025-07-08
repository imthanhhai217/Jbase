package com.juhalion.base.ui.fragments.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.juhalion.bae.events.SingleEvent
import com.juhalion.bae.networking.ApiResponse
import com.juhalion.base.models.comment.CommentResponse
import com.juhalion.base.repositories.CommentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
        private val commentRepo: CommentRepo, application: Application
) : AndroidViewModel(application) {

    private val _commentData = MutableLiveData<SingleEvent<ApiResponse<CommentResponse>>>()
    var commentData: LiveData<SingleEvent<ApiResponse<CommentResponse>>> = _commentData

    fun getComment() {
        _commentData.value = SingleEvent(ApiResponse.Loading())
        viewModelScope.launch {
            val response = commentRepo.getComments()
            _commentData.value = SingleEvent(response)
        }
    }
}