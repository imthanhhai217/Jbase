package com.juhalion.bae.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.juhalion.bae.base.BaseApplication
import com.juhalion.bae.config.CommonConfigManager
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preference<T>(
    private val name: String,
    private val default: T,
) : ReadWriteProperty<Any?, T> {
    var value: T by this

    companion object {
        private val prefs: SharedPreferences by lazy {
            val appContext = BaseApplication.instances ?: throw IllegalStateException("AiVideoApps.instances is null. "
                    + "Ensure AiVideoApps extends Application, assigns 'instances' in onCreate(), "
                    + "and is correctly declared in AndroidManifest.xml under <application android:name>.")
            appContext.getSharedPreferences(CommonConfigManager.getInstance().prefName, Context.MODE_PRIVATE)
        }

        fun clearAll() {
            prefs.edit(commit = true) { clear() }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return findPreference(name, default)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        putPreference(name, value)
    }

    @SuppressWarnings("unchecked_cast")
    private fun <T> findPreference(name: String, default: T): T = with(prefs) {
        return when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
        } as T
    }

    @SuppressLint("CommitPrefEdits")
    private fun <T> putPreference(name: String, value: T) = with(prefs.edit()) {
        synchronized(prefs) {
            when (value) {
                is Long -> putLong(name, value)
                is String -> putString(name, value)
                is Int -> putInt(name, value)
                is Boolean -> putBoolean(name, value)
                is Float -> putFloat(name, value)
                else -> throw IllegalArgumentException("This type cannot be saved into Preferences")
            }.commit()
        }
    }
}