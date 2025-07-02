package com.juhalion.base.mvvm.base.fragment_tab_view

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.juhalion.base.R

sealed class TabType(
    @StringRes val titleRes: Int,
    @DrawableRes val iconSelected: Int,
    @DrawableRes val iconUnselected: Int
) {
    object Home :
        TabType(titleRes = R.string.tab_home, iconSelected = R.drawable.ic_home_selected, iconUnselected = R.drawable.ic_home)

    object Profile :
        TabType(titleRes = R.string.tab_profile, iconSelected = R.drawable.ic_user_selected, iconUnselected = R.drawable.ic_user)

    object Play :
        TabType(titleRes = R.string.tab_play, iconSelected = R.drawable.ic_play_selected, iconUnselected = R.drawable.ic_play)

    object Settings :
        TabType(titleRes = R.string.tab_settings, iconSelected = R.drawable.ic_settings_selected, iconUnselected = R.drawable.ic_settings)
}