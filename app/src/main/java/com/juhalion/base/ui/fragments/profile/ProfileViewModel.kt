package com.juhalion.base.ui.fragments.profile

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.databinding.ObservableField
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.juhalion.base.R
import com.juhalion.base.models.product.Product
import com.juhalion.base.models.user.User
import com.juhalion.base.repositories.ProductRepository
import com.juhalion.base.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val application: Application, private val userRepository: UserRepository, private val productRepository: ProductRepository
) : AndroidViewModel(application) {
    private val TAG = "ProfileViewModel"
    private val _userData = MutableSharedFlow<List<User>>()
    val userData = _userData.asSharedFlow()

    val firebaseAuth: FirebaseAuth

    val email = ObservableField<String>()
    val password = ObservableField<String>()
    val isLogin = ObservableField<Boolean>()

    init {
        firebaseAuth = Firebase.auth
    }

    fun checkLoginState() = updateUI(firebaseAuth.currentUser)

    private val _loginMessage = MutableLiveData<String>()
    val loginMessage: LiveData<String> = _loginMessage

    fun createAccountWithEmail(activity: Activity) {
        val userEmail = email.get()
        if (userEmail.isNullOrEmpty()) {
            _loginMessage.value = "Please enter email"
            return
        }

        val userPassword = password.get()
        if (userPassword.isNullOrEmpty()) {
            _loginMessage.value = "Please enter password"
            return
        }

        firebaseAuth.createUserWithEmailAndPassword(userEmail.trim(), userPassword.trim()).addOnCompleteListener(activity) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "createUserWithEmail:success")
                _loginMessage.value = "createUserWithEmail:success"
                val user = firebaseAuth.currentUser
                updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                _loginMessage.value = "createUserWithEmail:failure, ${task.exception?.message}"
                updateUI(null)
            }
        }.addOnFailureListener {
            Log.w(TAG, "createUserWithEmail:failure ")
            updateUI(null)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            email.set(user.email)
            isLogin.set(true)
        } else {
            isLogin.set(false)
        }
    }

    fun logoutFirebase() {
        firebaseAuth.signOut()
        isLogin.set(false)
    }

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
            productRepository.getLocalProducts().collectLatest {
                _productData.emit(it)
            }
        }
    }

    fun signInWithGoogle(activity: FragmentActivity) {
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder().setFilterByAuthorizedAccounts(false)
            .setServerClientId(application.getString(R.string.default_web_client_id)).build()

        val request: GetCredentialRequest = GetCredentialRequest.Builder().addCredentialOption(googleIdOption).build()

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val credentialManager = CredentialManager.create(application)
                val result = credentialManager.getCredential(activity, request)
                handleSignIn(result.credential)
            } catch (e: Exception) {
                Log.e(TAG, "signInWithGoogle: ${e.message}")
                _loginMessage.postValue("signInWithGoogle: ${e.message}")
                updateUI(null)
            }
        }
    }

    private fun handleSignIn(credential: Credential) {
        when (credential) {
            is GoogleIdTokenCredential                  -> {
                val googleIdToken = credential.idToken
                val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
                firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "firebaseAuthWithGoogle:success")
                        _loginMessage.postValue("firebaseAuthWithGoogle:success")
                        updateUI(firebaseAuth.currentUser)
                    } else {
                        Log.w(TAG, "firebaseAuthWithGoogle:failure", task.exception)
                        _loginMessage.postValue("firebaseAuthWithGoogle:failure, ${task.exception?.message}")
                        updateUI(null)
                    }
                }
            }

            is PasswordCredential                       -> {
                //This is for email and password, which is handled separately
            }

            is CustomCredential, is PublicKeyCredential -> {
                Log.e(TAG, "Unexpected credential type")
                _loginMessage.postValue("Unexpected credential type")
                updateUI(null)
            }
        }
    }
}