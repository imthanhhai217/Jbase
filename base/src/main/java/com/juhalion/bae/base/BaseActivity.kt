package com.juhalion.bae.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BaseActivity<VB : ViewBinding> : AppCompatActivity() {

    private var _binding: VB? = null

    /** Access viewBinding only between onCreate - onDestroy */
    protected val binding: VB
        get() = _binding ?: throw IllegalStateException("Accessing binding outside of lifecycle")

    /** Subclass must implement viewBinding inflation */
    protected abstract fun inflateBinding(): VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
}