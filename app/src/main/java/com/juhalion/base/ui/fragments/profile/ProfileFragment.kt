package com.juhalion.base.ui.fragments.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.databinding.FragmentProfileBinding
import com.juhalion.base.models.comment.user.User
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

        profileViewModel.getListUser()
        profileViewModel.upsertUser(User(1, "Ju nè"))

        observeViewModel()
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            profileViewModel.userData.collectLatest { data ->
                Log.d(TAG, "observeViewModel: ${data}")
            }
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}