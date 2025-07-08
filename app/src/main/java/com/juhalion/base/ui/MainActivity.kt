package com.juhalion.base.ui

import android.os.Bundle
import android.util.Log
import com.juhalion.bae.base.BaseActivity
import com.juhalion.bae.view.fragment_tab_view.TabItem
import com.juhalion.base.R
import com.juhalion.base.bottom_nav_config.BottomNavConfig
import com.juhalion.base.databinding.ActivityMainBinding
import com.juhalion.base.ui.fragments.home.HomeFragment
import com.juhalion.base.ui.fragments.play.PlayFragment
import com.juhalion.base.ui.fragments.profile.ProfileFragment
import com.juhalion.base.ui.fragments.settings.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint class MainActivity : BaseActivity<ActivityMainBinding>() {
    private val TAG = "MainActivity"
    override fun inflateBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
    }

    private fun initView() {
        binding.apply {
            val listItem = listOf(
                TabItem(BottomNavConfig.Home, fragment = HomeFragment.Companion.newInstance()),
                TabItem(BottomNavConfig.Play, fragment = PlayFragment.Companion.newInstance()),
                TabItem(BottomNavConfig.Profile,
                        fragment = ProfileFragment.Companion.newInstance()),
                TabItem(BottomNavConfig.Settings,
                        fragment = SettingsFragment.Companion.newInstance()))

            ftvDemo.apply {
                setupTabView(fragmentManager = supportFragmentManager, containerID = R.id.container,
                             listItem = listItem)

                onTabSelecting = { tab ->
                    if (tab is BottomNavConfig) {
                        when (tab) {
                            BottomNavConfig.Home     -> {
                                Log.d(TAG, "initView: Home")
                                true
                            }

                            BottomNavConfig.Play     -> {
                                Log.d(TAG, "initView: Play")
                                true
                            }

                            BottomNavConfig.Profile  -> {
                                Log.d(TAG, "initView: Profile")
                                ftvDemo.updateBadge(BottomNavConfig.Profile, null)
                                true
                            }

                            BottomNavConfig.Settings -> {
                                Log.d(TAG, "initView: Settings")
                                true
                            }
                        }
                    } else {
                        false
                    }
                }

                updateBadge(BottomNavConfig.Profile, 10)
                attachSwipeGesture(binding.swipeWrapper)
            }
        }
    }

    fun setTitle(title: String) {
        supportActionBar?.title = title
    }
}