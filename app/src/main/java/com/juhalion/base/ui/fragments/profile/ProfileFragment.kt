package com.juhalion.base.ui.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.juhalion.bae.base.BaseFragment
import com.juhalion.bae.utils.JuExtendFunction.setOnSingleClickListener
import com.juhalion.base.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentProfileBinding.inflate(layoutInflater, container, false)

    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        profileViewModel.checkLoginState()
        observeViewModel()
    }

    private fun initView() {
        withBinding {
            viewModel = profileViewModel

            btnRegisterWithEmail.setOnSingleClickListener {
                createAccountWithEmail()
            }

            btnLogout.setOnSingleClickListener {
                signOut()
            }
        }
    }

    private fun signOut() {
        profileViewModel.logoutFirebase()
    }

    private fun createAccountWithEmail() {
        profileViewModel.createAccountWithEmail(requireActivity())
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            launch {
                profileViewModel.userData.collectLatest { data ->
                    Log.d(TAG, "observeViewModel: userData ${data}")
                    // Show lên view, etc,...
                }
            }
            launch {
                profileViewModel.productData.collectLatest { data ->
                    Log.d(TAG, "observeViewModel: productData ${data}")
                }
            }
        }

        profileViewModel.loginMessage.observe(viewLifecycleOwner) { message ->
            Toast.makeText(context, "$message", Toast.LENGTH_SHORT).show()
        }
    }


    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}