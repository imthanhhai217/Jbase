package com.juhalion.base.ui.fragments.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.juhalion.base.models.product.Product
import com.juhalion.base.models.user.User
import com.juhalion.base.repositories.ProductRepository
import com.juhalion.base.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
        application: Application,
        private val userRepository: UserRepository,
        private val productRepository: ProductRepository
) : AndroidViewModel(application) {

    private val _userData = MutableSharedFlow<List<User>>()
    val userData = _userData.asSharedFlow()

    fun getListUser() {
        viewModelScope.launch {
            userRepository.getAllUsers().collectLatest { listData ->
                _userData.emit(listData)
            }
        }
    }

    fun upsertUser(user: User) {
        viewModelScope.launch {
            userRepository.upsertUser(user)
        }
    }

    fun deleteUser(user: User) {
        viewModelScope.launch {
            userRepository.deleteUser(user)
        }
    }

    fun deleteUser(userID: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(userID)
        }
    }

    fun upsertProduct(product: Product) {
        viewModelScope.launch {
            productRepository.upsertProduct(product)
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            productRepository.deleteProduct(product)
        }
    }

    fun deleteProduct(productID: Int) {
        viewModelScope.launch {
            productRepository.deleteProduct(productID)
        }
    }

    private val _productData = MutableSharedFlow<List<Product>>()
    val productData = _productData.asSharedFlow()

    fun getListProduct() {
        viewModelScope.launch {
            productRepository.getAllProducts().collectLatest {
                _productData.emit(it)
            }
        }
    }

}