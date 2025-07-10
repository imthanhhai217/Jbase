package com.juhalion.bae.view.gesture_detector

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.HorizontalScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2

class SwipeInterceptFrameLayout @JvmOverloads constructor(
    context: Context, attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    var swipeListener: ((MotionEvent) -> Unit)? = null

    private val ignoreSwipeView = mutableMapOf<View, OnAttachStateChangeListener>()

    fun addIgnoreSwipeView(view: View) {
        if (ignoreSwipeView.contains(view)) return

        val listener = object : OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {}

            override fun onViewDetachedFromWindow(v: View) {
                ignoreSwipeView.remove(v)
                v.removeOnAttachStateChangeListener(this)
            }
        }

        view.addOnAttachStateChangeListener(listener)
        ignoreSwipeView[view] = listener
    }

    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        ev?.let { event ->
            val eventAction = event.action
            if (eventAction == MotionEvent.ACTION_DOWN) {
                findingChildrenViewTouched(event)?.let { touchedView ->
                    if (isTouchedOnIgnoreView(touchedView)) {
                        parent?.requestDisallowInterceptTouchEvent(true)
                    }
                }
            }
            swipeListener?.invoke(event)
        }
        return super.onInterceptTouchEvent(ev)
    }

    private fun isTouchedOnIgnoreView(view: View): Boolean {
        val isIgnoredView = ignoreSwipeView.contains(view)
        return isIgnoredView || isViewScrollableHorizontally(view)
    }

    private fun isViewScrollableHorizontally(view: View): Boolean {
        return when (view) {
            is ViewPager            -> true
            is ViewPager2           -> true
            is HorizontalScrollView -> true
            is RecyclerView         -> {
                val lm = view.layoutManager
                (lm is LinearLayoutManager && lm.orientation == RecyclerView.HORIZONTAL) || (lm is GridLayoutManager && lm.orientation == RecyclerView.HORIZONTAL)
            }

            else                    -> false
        }
    }

    private fun findingChildrenViewTouched(event: MotionEvent): View? {
        val x = event.x
        val y = event.y
        for (i in 0 until childCount) {
            val child = getChildAt(i)
            if (isTouchedInsideView(x, y, child)) return findDeepestChild(x, y, child)
        }
        return null
    }

    private fun findDeepestChild(
        x: Float, y: Float, view: View
    ): View? {
        if (view !is ViewGroup) return view

        for (i in 0 until view.childCount) {
            val child = view.getChildAt(i)
            if (isTouchedInsideView(x - view.left, y - view.top, child)) return findDeepestChild(x - view.left, y - view.top, child)
        }
        return view
    }

    private fun isTouchedInsideView(x: Float, y: Float, view: View): Boolean {
        val rect = Rect()
        view.getHitRect(rect)
        return rect.contains(x.toInt(), y.toInt())
    }

    @SuppressLint("ClickableViewAccessibility") override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            swipeListener?.invoke(it) }
        return super.onTouchEvent(event)
    }
}