package com.juhalion.bae.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.juhalion.bae.base.BaseApplication
import com.juhalion.bae.config.CommonConfigManager
import java.security.GeneralSecurityException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private const val SECURE_PREFS_TAG = "EncryptedPreference"

@Suppress("UNCHECKED_CAST")
class EncryptedPreference<T>(
    private val name: String,
    private val default: T,
) : ReadWriteProperty<Any?, T> {

    companion object {
        private val PREF_NAME_SECURE = CommonConfigManager.getInstance().prefNameSecure ?: ""
        private val appContext: Context by lazy {
            BaseApplication.instances ?: throw IllegalStateException("AiVideoApps.instances is null. " + "Ensure AiVideoApps extends Application, assigns 'instances' in onCreate(), " + "and is correctly declared in AndroidManifest.xml.")
        }

        private fun createActualEncryptedSharedPreferences(context: Context): SharedPreferences {
            val masterKey = MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
            return EncryptedSharedPreferences.create(context,
                PREF_NAME_SECURE,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
        }

        internal fun resetEncryptedData(context: Context) {
            Log.w(SECURE_PREFS_TAG, "Resetting encrypted SharedPreferences: ${PREF_NAME_SECURE}")
            try {
                context.getSharedPreferences(PREF_NAME_SECURE, Context.MODE_PRIVATE).edit { clear() }
            } catch (e: Exception) {
                Log.e(SECURE_PREFS_TAG, "Failed to clear encrypted SharedPreferences during reset: ${e.message}", e)
            }
        }

        val prefs: SharedPreferences by lazy {
            try {
                createActualEncryptedSharedPreferences(appContext)
            } catch (e: Exception) {
                Log.e(SECURE_PREFS_TAG, "Initial creation of EncryptedSharedPreferences failed, resetting and retrying. Error: ${e.message}", e)
                resetEncryptedData(appContext)
                // Attempt to create again after reset. If this fails, it will throw.
                createActualEncryptedSharedPreferences(appContext)
            }
        }

        fun clearAll() {
            Log.i(SECURE_PREFS_TAG, "Clearing all encrypted preferences.")
            // Resetting will clear and force re-initialization on next access if Keystore was the issue
            // If we just want to clear data without implying Keystore issues, a simple clear is fine
            // However, to mimic full reset behavior, re-trigger lazy init after clear:
            resetEncryptedData(appContext)
            // To ensure re-initialization logic is hit if lazy is already initialized:
            // This is a bit of a hack. A cleaner way would be to expose a re-init or make prefs a function.
            // For simplicity with 'by lazy', clearing is enough if values are re-queried.
            // If 'prefs' itself needs to be re-evaluated for keystore validation, then it's more complex.
            // The current 'resetEncryptedData' + 'prefs' lazy init handles this.
            prefs.edit { clear() } // Direct clear after ensuring it's initialized (or re-initialized)
        }

        fun remove(key: String) {
            try {
                prefs.edit { remove(key) }
            } catch (e: GeneralSecurityException) {
                Log.e(SECURE_PREFS_TAG, "SecurityException on removing key '$key', resetting data. Error: ${e.message}", e)
                resetEncryptedData(appContext)
            } catch (e: Exception) {
                Log.e(SECURE_PREFS_TAG, "Generic exception on removing key '$key'. Error: ${e.message}", e)
            }
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return try {
            when (default) {
                is Long -> prefs.getLong(name, default)
                is String -> prefs.getString(name, default)
                is Int -> prefs.getInt(name, default)
                is Boolean -> prefs.getBoolean(name, default)
                is Float -> prefs.getFloat(name, default)
                else -> throw IllegalArgumentException("This type cannot be saved into EncryptedPreferences")
            } as T
        } catch (e: GeneralSecurityException) {
            Log.e(SECURE_PREFS_TAG,
                "SecurityException on getting value for key '$name', resetting data and returning default. Error: ${e.message}",
                e)
            resetEncryptedData(appContext)
            default
        } catch (e: ClassCastException) {
            Log.e(SECURE_PREFS_TAG, "ClassCastException for key '$name', returning default. Check stored type. Error: ${e.message}", e)
            default
        } catch (e: Exception) {
            Log.e(SECURE_PREFS_TAG, "Generic exception on getting value for key '$name', returning default. Error: ${e.message}", e)
            default
        }
    }

    @SuppressLint("CommitPrefEdits")
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        try {
            prefs.edit(commit = true) { // commit=true for immediate write, can be false for apply()
                when (value) {
                    is Long -> putLong(name, value)
                    is String -> putString(name, value)
                    is Int -> putInt(name, value)
                    is Boolean -> putBoolean(name, value)
                    is Float -> putFloat(name, value)
                    else -> throw IllegalArgumentException("This type cannot be saved into EncryptedPreferences")
                }
            }
        } catch (e: GeneralSecurityException) {
            Log.e(SECURE_PREFS_TAG, "SecurityException on setting value for key '$name', resetting data. Value not saved. Error: ${e.message}", e)
            resetEncryptedData(appContext)
        } catch (e: Exception) {
            Log.e(SECURE_PREFS_TAG, "Generic exception on setting value for key '$name'. Value not saved. Error: ${e.message}", e)
        }
    }
}

object SecureSharedPrefs {
    /**
     * Clears all data stored in these EncryptedSharedPreferences.
     * This will also trigger the reset and re-initialization logic if there
     * were issues with the Keystore.
     */
    fun clearAll() {
        EncryptedPreference.clearAll()
    }

    /**
     * Removes a specific key-value pair from EncryptedSharedPreferences.
     * @param key The key to remove (should be one of PrefKey constants used by delegates).
     */
    fun remove(key: String) {
        EncryptedPreference.remove(key)
    }
}

