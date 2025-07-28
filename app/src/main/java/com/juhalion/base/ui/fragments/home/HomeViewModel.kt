package com.juhalion.base.ui.fragments.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.juhalion.bae.events.SingleEvent
import com.juhalion.bae.networking.ApiResponse
import com.juhalion.base.models.comment.CommentResponse
import com.juhalion.base.models.product.ProductResponse
import com.juhalion.base.repositories.CommentRepo
import com.juhalion.base.repositories.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application, private val commentRepo: CommentRepo, private val productRepo: ProductRepository
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

    private val _productData = MutableLiveData<SingleEvent<ApiResponse<ProductResponse>>>()
    var productData: LiveData<SingleEvent<ApiResponse<ProductResponse>>> = _productData
    fun getProducts() {
        _productData.value = SingleEvent(ApiResponse.Loading())

        viewModelScope.launch {
            val response = productRepo.fetchListProduct()
            _productData.value = SingleEvent(response)
        }
    }
}