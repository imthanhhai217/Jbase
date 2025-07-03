package com.juhalion.base.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.databinding.FragmentProfileBinding

class ProfileFragment : BaseFragment<FragmentProfileBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentProfileBinding.inflate(layoutInflater, container, false)


    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}