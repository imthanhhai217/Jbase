package com.juhalion.base.ui.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.databinding.FragmentSettingsBinding

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(layoutInflater, container, false)

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}