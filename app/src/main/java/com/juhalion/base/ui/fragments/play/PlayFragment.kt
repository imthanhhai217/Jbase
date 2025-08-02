package com.juhalion.base.ui.fragments.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.databinding.FragmentPlayBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayFragment : BaseFragment<FragmentPlayBinding>() {
    override fun inflateBinding(
            inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentPlayBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance() = PlayFragment()
    }
}