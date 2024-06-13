package com.juhalion.base.mvvm.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.juhalion.base.mvvm.models.response.comments.Comment
import com.juhalion.base.mvvm.models.response.comments.CommentResponse
import com.juhalion.base.mvvm.networking.ApiResponse
import com.juhalion.base.mvvm.repositories.CommentRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(private val commentRepo: CommentRepo, application: Application) : AndroidViewModel(application) {

    private var commentData = MutableLiveData<ApiResponse<CommentResponse>>()

    fun getComment():LiveData<ApiResponse<CommentResponse>>{
        commentData.value = ApiResponse.Loading()
        viewModelScope.launch{
            val response = commentRepo.getComments()
            commentData.value = response
        }
        return commentData
    }
}