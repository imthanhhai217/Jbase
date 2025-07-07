package com.juhalion.base.ui.fragments.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.juhalion.bae.events.SingleEvent
import com.juhalion.base.models.product.Product
import com.juhalion.base.models.user.User
import com.juhalion.base.repositories.ProductRepository
import com.juhalion.base.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val application: Application,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) : AndroidViewModel(application) {

    private val _userData = MutableSharedFlow<List<User>>()
    val userData = _userData.asSharedFlow()
//    Flow -. Bắn dữ liệu từ A -> B
//    SharedFlow Bắn được dữ liệu từ a->B
//    C muốn lắng nghe value của A

    private val _demo = MutableStateFlow<List<User>>(emptyList())
    val demo: StateFlow<List<User>> = _demo.asStateFlow()

    private val _demoLiveData = MutableLiveData<Any>()
    val demoLiveData: LiveData<Any> = _demoLiveData

    private val _singleLiveData = MutableLiveData<SingleEvent<Any>>()
    val singleLiveData: LiveData<SingleEvent<Any>> = _singleLiveData


    fun getListUser() {
        viewModelScope.launch {
            userRepository.getAllUsers().collectLatest { listData ->
                _userData.emit(listData)
            }
        }
    }

    fun updateDemoLiveData() {
        _demoLiveData.value = Any()
    }

    fun updateDemoSingleLiveData() {
        _singleLiveData.value = SingleEvent(Any())
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