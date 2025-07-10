package com.juhalion.bae.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppLifecycleObserver @Inject constructor() : DefaultLifecycleObserver {

    var isAppInForeground: Boolean = false
        private set
    var isAppRunning: Boolean = false
        private set

    override fun onStart(owner: LifecycleOwner) {
        super.onStart(owner)
        isAppInForeground = true
        isAppRunning = true  // Khi ứng dụng bắt đầu, đánh dấu ứng dụng đang mở
    }

    override fun onStop(owner: LifecycleOwner) {
        super.onStop(owner)
        isAppInForeground = false
    }

    fun onAppTerminate() {
        // Thêm logic khi ứng dụng kết thúc hoặc ngừng chạy
        isAppRunning = false
    }
}