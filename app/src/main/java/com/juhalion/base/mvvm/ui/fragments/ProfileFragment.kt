package com.juhalion.base.mvvm.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.juhalion.base.databinding.FragmentProfileBinding
import com.juhalion.base.mvvm.base.BaseFragment

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentProfileBinding.inflate(layoutInflater, container, false)


    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}