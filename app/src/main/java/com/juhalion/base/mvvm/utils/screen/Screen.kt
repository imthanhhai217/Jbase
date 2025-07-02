package com.juhalion.base.mvvm.utils.screen

data class Screen(val data: HashMap<ScreenKey, Int>) {
    private val VALUE = 1
    fun getHeight(): Int {
        return data[ScreenKey.HEIGHT] ?: VALUE
    }

    fun getWidth(): Int {
        return data[ScreenKey.WIDTH] ?: VALUE
    }
}