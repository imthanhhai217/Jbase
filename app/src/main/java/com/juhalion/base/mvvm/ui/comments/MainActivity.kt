package com.juhalion.base.mvvm.ui.comments

import android.os.Bundle
import android.util.Log
import com.juhalion.base.R
import com.juhalion.base.databinding.ActivityMainBinding
import com.juhalion.base.mvvm.base.BaseActivity
import com.juhalion.base.mvvm.base.fragment_tab_view.TabItem
import com.juhalion.base.mvvm.base.fragment_tab_view.TabType
import com.juhalion.base.mvvm.ui.fragments.HomeFragment
import com.juhalion.base.mvvm.ui.fragments.PlayFragment
import com.juhalion.base.mvvm.ui.fragments.ProfileFragment
import com.juhalion.base.mvvm.ui.fragments.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val TAG = "MainActivity"
    override fun inflateBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.apply {
            val listItem = listOf(
                TabItem(tabType = TabType.Home, fragment = HomeFragment.newInstance()),
                TabItem(tabType = TabType.Play, fragment = PlayFragment.newInstance()),
                TabItem(tabType = TabType.Profile, fragment = ProfileFragment.newInstance()),
                TabItem(tabType = TabType.Settings, fragment = SettingsFragment.newInstance())
            )

            ftvDemo.apply {
                setupTabView(
                    fragmentManager = supportFragmentManager,
                    containerID = R.id.container,
                    listItem = listItem
                )

                onTabSelecting = { tab ->
                    when (tab) {
                        TabType.Home -> {
                            Log.d(TAG, "initView: Home")
                            true
                        }

                        TabType.Play -> {
                            Log.d(TAG, "initView: Play")
                            true
                        }

                        TabType.Profile -> {
                            Log.d(TAG, "initView: Profile")
                            ftvDemo.updateBadge(TabType.Profile, null)
                            true
                        }

                        TabType.Settings -> {
                            Log.d(TAG, "initView: Settings")
                            true
                        }
                    }
                }

                ftvDemo.updateBadge(TabType.Profile, 10)
            }
        }
    }

    fun setTitle(title: String) {
        supportActionBar?.title = title
    }
}