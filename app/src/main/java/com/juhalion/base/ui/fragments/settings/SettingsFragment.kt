package com.juhalion.base.ui.fragments.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import com.juhalion.bae.base.BaseFragment
import com.juhalion.base.adapters.FunctionAdapterMultiType
import com.juhalion.base.constants.Constant
import com.juhalion.base.databinding.FragmentSettingsBinding
import com.juhalion.base.models.function.JFunction
import androidx.core.content.edit
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun inflateBinding(
        inflater: LayoutInflater, container: ViewGroup?
    ) = FragmentSettingsBinding.inflate(layoutInflater, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Initialize SharedPreferences
        sharedPreferences = requireActivity().getSharedPreferences(Constant.PREF_NAME, Context.MODE_PRIVATE)
        initView()
    }

    private fun generateFunctionList(): List<JFunction> {
        val isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false)

        return listOf(
            JFunction.HeaderItem(id = 1, title = "Tính năng phổ biến"),
            JFunction.FunctionItem(id = 2, title = "Chụp ảnh"),
            JFunction.FunctionItem(id = 3, title = "Quét mã QR"),
            JFunction.FunctionItem(id = 4, title = "Tạo mã QR"),

            JFunction.HeaderItem(id = 5, title = "Tiện ích khác"),
            JFunction.SwitchItem(id = 100, title = "Chế độ tối", isChecked = isDarkMode), // Dark Mode Switch
            JFunction.FunctionItem(id = 6, title = "Cài đặt"),
            JFunction.FunctionItem(id = 7, title = "Lịch sử"),
            JFunction.FunctionItem(id = 8, title = "Giới thiệu"),

            JFunction.HeaderItem(id = 9, title = "Tính năng phổ biến 2"),
            JFunction.FunctionItem(id = 10, title = "Chụp ảnh 2"),
            JFunction.FunctionItem(id = 11, title = "Quét mã QR 2"),
            JFunction.FunctionItem(id = 12, title = "Tạo mã QR 2"),

            JFunction.HeaderItem(id = 13, title = "Tiện ích khác 3"),
            JFunction.FunctionItem(id = 14, title = "Cài đặt 3"),
            JFunction.FunctionItem(id = 15, title = "Lịch sử 3"),
            JFunction.FunctionItem(id = 16, title = "Giới thiệu 3"),
        )
    }

    private fun initView() {
        withBinding {
            val functionAdapter = FunctionAdapterMultiType()
            rvMultiType.adapter = functionAdapter
            val functionAdapter2 = FunctionAdapterMultiType()
            rvMultiType2.adapter = functionAdapter2

            functionAdapter.apply {
                updateData(generateFunctionList())
                listener = { _, item, _ ->
                    if (item is JFunction.SwitchItem) {
                        handleThemeChange(item.isChecked)
                    }
                }
            }

            functionAdapter2.apply {
                updateData(generateFunctionList())
                listener = { _, item, _ ->
                     if (item is JFunction.SwitchItem) {
                        handleThemeChange(item.isChecked)
                    }
                }
            }
        }
    }

    private fun handleThemeChange(isDarkMode: Boolean) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        // Save the setting
        sharedPreferences.edit { putBoolean(KEY_DARK_MODE, isDarkMode) }
    }

    companion object {
        const val KEY_DARK_MODE = "dark_mode_enabled"
        @JvmStatic fun newInstance() = SettingsFragment()
    }
}
