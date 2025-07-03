package com.juhalion.base.bottom_nav_config

import com.juhalion.bae.view.fragment_tab_view.TabInfo
import com.juhalion.base.R

sealed class BottomNavConfig(
) : TabInfo {
    object Home : BottomNavConfig() {
        override val titleRes = R.string.tab_home
        override val iconSelected = R.drawable.ic_home_selected
        override val iconUnselected = R.drawable.ic_home
    }

    object Profile : BottomNavConfig() {
        override val titleRes = R.string.tab_profile
        override val iconSelected = R.drawable.ic_user_selected
        override val iconUnselected = R.drawable.ic_user
    }

    object Play : BottomNavConfig() {
        override val titleRes = R.string.tab_play
        override val iconSelected = R.drawable.ic_play_selected
        override val iconUnselected = R.drawable.ic_play
    }

    object Settings : BottomNavConfig() {
        override val titleRes = R.string.tab_settings
        override val iconSelected = R.drawable.ic_settings_selected
        override val iconUnselected = R.drawable.ic_settings
    }
}