package com.juhalion.bae.utils.single_click

object ClickDebounceManager {
    private var lastClickTime = 0L

    /**
     * Return true nếu được phép click, false nếu đang debounce.
     */
    @Synchronized
    fun canClick(debounceTime: Long = 300L): Boolean {
        val currentTime = System.currentTimeMillis()
        return if (currentTime - lastClickTime >= debounceTime) {
            lastClickTime = currentTime
            true
        } else {
            false
        }
    }

    fun reset() {
        lastClickTime = 0L
    }
}