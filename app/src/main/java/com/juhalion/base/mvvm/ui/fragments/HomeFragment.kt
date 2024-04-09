package com.juhalion.base.mvvm.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.callscreen.caller.basemvvm.R
import com.callscreen.caller.basemvvm.databinding.FragmentHomeBinding
import com.juhalion.base.mvvm.ui.activities.MainActivity
import com.juhalion.base.mvvm.ui.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {
    override fun inflateBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentHomeBinding.inflate(layoutInflater)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).setTitle(getString(R.string.tool_bar_title))
    }
}