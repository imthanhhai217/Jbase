package com.juhalion.base.mvvm.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.juhalion.base.databinding.FragmentPlayBinding
import com.juhalion.base.mvvm.base.BaseFragment

class PlayFragment : BaseFragment<FragmentPlayBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPlayBinding.inflate(layoutInflater, container, false)

    companion object {

        @JvmStatic
        fun newInstance() = PlayFragment()
    }
}