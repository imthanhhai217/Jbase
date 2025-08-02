package com.juhalion.bae.base

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.juhalion.bae.events.SingleEvent
import com.juhalion.bae.networking.UnAuthenticationCallback

abstract class BaseApplication : Application(), UnAuthenticationCallback {
    abstract fun getInstances(): BaseApplication
    abstract fun isAppRunning(): Boolean
    abstract fun isAppInForeground(): Boolean
    abstract val unAuthenticationEvent: MutableLiveData<SingleEvent<Any>>
    abstract val notificationComing: MutableLiveData<SingleEvent<Any>>
    abstract fun sendNotificationComing()
}