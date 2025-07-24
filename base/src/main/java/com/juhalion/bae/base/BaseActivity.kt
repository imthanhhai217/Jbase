package com.juhalion.bae.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewbinding.ViewBinding

object AppConfig {
    var isNightMode: Boolean = false
}

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null

    /** Access viewBinding only between onCreate - onDestroy */
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("Accessing binding outside of lifecycle")

    /** Subclass must implement viewBinding inflation */
    protected abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyTheme()
        _binding = inflateBinding()
        setContentView(binding.root)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /** Optional: DSL style for using binding */
    protected inline fun <T> withBinding(block: VB.() -> T): T {
        return binding.block()
    }

    private fun applyTheme() {
        if (AppConfig.isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }
}