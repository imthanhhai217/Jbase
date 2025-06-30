package com.juhalion.base.mvvm.ui.comments

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.juhalion.base.mvvm.models.response.comments.CommentResponse
import com.juhalion.base.mvvm.networking.ApiResponse
import com.juhalion.base.mvvm.repositories.CommentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val commentRepo: CommentRepo, application: Application) : AndroidViewModel(application) {

    var commentData = MutableLiveData<ApiResponse<CommentResponse>>()

    fun getComment(){
        commentData.postValue(ApiResponse.Loading())
        viewModelScope.launch {
            val response = commentRepo.getComments()
            commentData.postValue(response)
        }
    }
}