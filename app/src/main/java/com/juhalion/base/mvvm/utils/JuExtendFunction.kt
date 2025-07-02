package com.juhalion.base.mvvm.utils

import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Build
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.juhalion.base.mvvm.utils.screen.Screen
import com.juhalion.base.mvvm.utils.screen.ScreenKey
import com.juhalion.base.mvvm.utils.single_click.ClickDebounceManager
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

object JuExtendFunction {
    private const val TAG = "JuExtendFunction"

    fun View.visible() {
        this.visibility = View.VISIBLE
    }

    fun View.gone() {
        this.visibility = View.GONE
    }

    fun View.invisible() {
        this.visibility = View.INVISIBLE
    }

    fun View.setVisibleOrGone(visible: Boolean) {
        if (visible) visible() else gone()
    }

    fun View.setVisibleOrInvisible(visible: Boolean) {
        if (visible) visible() else invisible()
    }

    fun AppCompatActivity.applyMyActivitySetting() {
        this.supportActionBar?.hide()
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }

    @OptIn(ExperimentalContracts::class)
    inline fun View.setOnSingleClickListener(
        debounceTime: Long = 300L,
        crossinline onClick: (View) -> Unit
    ) {
        contract {
            callsInPlace(onClick, InvocationKind.AT_MOST_ONCE)
        }

        this.setOnClickListener { view ->
            if (!ClickDebounceManager.canClick(debounceTime)) return@setOnClickListener
            onClick.invoke(view)
        }
    }


    fun View.enable() {
        this.apply {
            isEnabled = true
            alpha = 1f
        }
    }

    fun View.disable(opacity: Float? = null) {
        this.apply {
            isEnabled = false
            alpha = opacity ?: 0.5f
        }
    }

    fun Context.getCompatColor(resID: Int) = ContextCompat.getColor(this, resID)
    fun Context.getCompatString(resID: Int) = ContextCompat.getString(this, resID)
    fun Context.getCompatDrawable(resID: Int) = ContextCompat.getDrawable(this, resID)

    fun Activity.isLive(callback: (() -> Unit)? = null): Boolean {
        val isLive = !isFinishing && !isDestroyed
        if (isLive) {
            callback?.invoke()
        } else {
            Log.d(TAG, "isLive: $localClassName is not live")
        }
        return isLive
    }

    fun Fragment.isLive(callback: (() -> Unit)? = null): Boolean {
        val isLive =
            isAdded && !isRemoving && !isDetached && !isStateSaved && activity?.let { !it.isDestroyed && !it.isFinishing } == true

        Log.d("Fragment.isLive", "isLive: $isLive (${this::class.java.simpleName})")

        if (isLive) callback?.invoke()
        return isLive
    }

    fun View.showKeyboard(activity: Activity) {
        activity.isLive {
            this.requestFocus()
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    fun View.hideKeyboard(activity: Activity) {
        activity.isLive {
            this.clearFocus()
            val imm = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(this.windowToken, 0)
        }
    }

    fun Int?.isNullOrZero() = this == null || this == 0

    fun Activity.getScreenInformation(): Screen {
        val res = HashMap<ScreenKey, Int>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = windowManager.currentWindowMetrics
            val bounds = windowMetrics.bounds
            res[ScreenKey.HEIGHT] = bounds.height()
            res[ScreenKey.WIDTH] = bounds.width()
        } else {
            val dm = DisplayMetrics()
            @Suppress("DEPRECATION") windowManager.defaultDisplay.getMetrics(dm)
            res[ScreenKey.HEIGHT] = dm.heightPixels
            res[ScreenKey.WIDTH] = dm.widthPixels
        }
        return Screen(res)
    }

    fun Guideline.updateGuidePercent(percent: Float) {
        (layoutParams as? ConstraintLayout.LayoutParams)?.let {
            it.guidePercent = percent
            layoutParams = it
        }
    }

    fun Float.dpToPx(context: Context): Int =
        (this * context.resources.displayMetrics.density).toInt()

    fun Float.spToPx(context: Context): Float =
        this * context.resources.displayMetrics.scaledDensity
}