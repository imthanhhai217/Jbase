package com.juhalion.bae.base

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragment<VB : ViewBinding> : Fragment() {
    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding!!
    private lateinit var mProgressDialog: ProgressDialog

    abstract fun inflateBinding(inflater: LayoutInflater, container: ViewGroup?): VB

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        _binding = inflateBinding(inflater, container)
        mProgressDialog = ProgressDialog(activity)
        mProgressDialog.setMessage("Loading...")
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showLoadingDialog() {
        if (!mProgressDialog.isShowing) {
            mProgressDialog.apply {
                show()
                setCancelable(false)
                setCanceledOnTouchOutside(false)
            }
        }
    }

    fun dismissLoadingDialog() {
        if (mProgressDialog.isShowing) {
            mProgressDialog.dismiss()
        }
    }
}