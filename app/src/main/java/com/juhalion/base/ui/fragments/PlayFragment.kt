package com.juhalion.base.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.databinding.FragmentPlayBinding

class PlayFragment : BaseFragment<FragmentPlayBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPlayBinding.inflate(layoutInflater, container, false)

    companion object {

        @JvmStatic
        fun newInstance() = PlayFragment()
    }
}