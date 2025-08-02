package com.juhalion.bae.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    protected val TAG = this::class.java.simpleName
    private var _binding: VB? = null

    /** Access viewBinding only between onCreateView - onDestroyView */
    protected val binding: VB
        get() = _binding
            ?: throw IllegalStateException("Accessing binding outside of view lifecycle")

    /** Subclass must implement viewBinding inflation */
    protected abstract fun inflateBinding(
            inflater: LayoutInflater, container: ViewGroup?
    ): VB

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = inflateBinding(inflater, container)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /** Optional: DSL style for using binding */
    protected inline fun <T> withBinding(block: VB.() -> T): T {
        return binding.block()
    }
}