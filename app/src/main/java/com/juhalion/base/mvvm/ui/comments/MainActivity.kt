package com.juhalion.base.mvvm.ui.comments

import android.os.Bundle
import com.callscreen.caller.basemvvm.R
import com.callscreen.caller.basemvvm.databinding.ActivityMainBinding
import com.juhalion.base.mvvm.base.BaseActivity
import com.juhalion.base.mvvm.ui.fragments.HomeFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    override fun inflateBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val toolbar = binding.iToolbar.toolBar
        setSupportActionBar(toolbar)

        addFragment(HomeFragment(), getString(R.string.home_fragment_tag))
    }

    fun setTitle(title: String) {
        supportActionBar?.title = title
    }
}