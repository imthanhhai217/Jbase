package com.juhalion.base.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.adapters.FunctionAdapterMultiType
import com.juhalion.base.databinding.FragmentSettingsBinding
import com.juhalion.base.models.function.JFunction

class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(layoutInflater, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    fun generateFakeFunctionList(): List<JFunction> {
        return listOf(
            JFunction.HeaderItem(id = 1, title = "Tính năng phổ biến"),
            JFunction.FunctionItem(id = 2, title = "Chụp ảnh"),
            JFunction.FunctionItem(id = 3, title = "Quét mã QR"),
            JFunction.FunctionItem(id = 4, title = "Tạo mã QR"),

            JFunction.HeaderItem(id = 5, title = "Tiện ích khác"),
            JFunction.FunctionItem(id = 6, title = "Cài đặt"),
            JFunction.FunctionItem(id = 7, title = "Lịch sử"),
            JFunction.FunctionItem(id = 8, title = "Giới thiệu"),
        )
    }

    private fun initView() {
        withBinding {
            val functionAdapter = FunctionAdapterMultiType()
            rvMultiType.adapter = functionAdapter

            functionAdapter.apply {
                updateData(generateFakeFunctionList())
                listener = { view, item, position ->

                }
            }

        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = SettingsFragment()
    }
}