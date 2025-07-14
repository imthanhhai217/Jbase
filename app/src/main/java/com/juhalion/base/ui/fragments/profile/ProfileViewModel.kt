package com.juhalion.base.ui.fragments.profile

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.core.content.ContextCompat.getString
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
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
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
    private val application: Application,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
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
    private lateinit var credentialManager: CredentialManager

    fun signInWithGoogle(activity: FragmentActivity) {
        credentialManager = CredentialManager.create(activity)

        val googleIdOption = GetGoogleIdOption.Builder()
            // Your server's client ID, not your Android client ID.
            .setServerClientId(getString(application, R.string.default_web_client_id))
            .setFilterByAuthorizedAccounts(true)
            .setAutoSelectEnabled(true)
            .build()

        // Create the Credential Manager request
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        viewModelScope.launch(Dispatchers.Main) {
            try {
                val result = credentialManager.getCredential(activity, request)
                handleSignIn(activity, result.credential)
            } catch (e: Exception) {
                Log.d(TAG, "signInWithGoogle: ${e.message}")
            }
        }
    }

    fun handleSignIn(activity: Activity, result: Credential) {
        // Handle the successfully returned credential.
        val credential = result
        val responseJson: String

        when (credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse to your server to validate and
                // authenticate
                responseJson = credential.authenticationResponseJson
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        // You can use the members of googleIdTokenCredential directly for UX
                        // purposes, but don't use them to store or control access to user
                        // data. For that you first need to validate the token:
                        // pass googleIdTokenCredential.getIdToken() to the backend server.
                        // see [validation instructions](https://developers.google.com/identity/gsi/web/guides/verify-google-id-token)

                        firebaseAuthWithGoogle(activity, googleIdTokenCredential.idToken)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun firebaseAuthWithGoogle(activity: Activity, idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(activity) { task ->
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
        }
    }


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

        firebaseAuth.createUserWithEmailAndPassword(userEmail.trim(), userPassword.trim())
            .addOnCompleteListener(activity) { task ->
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
            productRepository.getAllProducts().collectLatest {
                _productData.emit(it)
            }
        }
    }
}