package com.juhalion.bae.view.fragment_tab_view.animation

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class PageTransformer(private val pageAnimationType: PageAnimationType) : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        when (pageAnimationType) {
            PageAnimationType.NONE            -> {
                page.alpha = 1f
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
                page.rotationY = 0f
            }

            PageAnimationType.ZOOM_OUT        -> {
                val scale = 0.9f.coerceAtLeast(1 - abs(position))
                page.scaleX = scale
                page.scaleY = scale
                page.alpha = 0.5f + (scale - 0.9f) / 0.15f * 0.5f
            }

            PageAnimationType.DEPTH           -> {
                when {
                    position <= 0f -> {
                        page.translationX = 0f
                        page.scaleX = 1f
                        page.scaleY = 1f
                        page.alpha = 1f
                    }

                    position <= 1f -> {
                        page.translationX = -position * page.width
                        val scale = 0.75f + (1 - position) * 0.25f
                        page.scaleX = scale
                        page.scaleY = scale
                        page.alpha = 1 - position
                    }

                    else           -> page.alpha = 0f
                }
            }

            PageAnimationType.FADE            -> {
                page.alpha = 1 - abs(position)
                page.translationX = 0f
                page.scaleX = 1f
                page.scaleY = 1f
            }

            PageAnimationType.SLIDE_FADE      -> {
                page.translationX = -position * page.width
                page.alpha = 1 - abs(position)
            }

            PageAnimationType.POP             -> {
                val scale = 1f + 0.15f * (1 - abs(position))
                page.scaleX = scale
                page.scaleY = scale
                page.alpha = 1f - abs(position) * 0.5f

                // Optionally shift page slightly for depth feel
                page.translationY = -abs(position) * 20
            }

            PageAnimationType.OVERLAP         -> {
                val scale = 0.8f + (1 - abs(position)) * 0.2f
                page.scaleX = scale
                page.scaleY = scale
                page.translationX = -position * page.width * 0.3f
                page.alpha = 1 - abs(position) * 0.3f
            }

            PageAnimationType.CUBE            -> {
                page.cameraDistance = 10000f
                if (position < -1 || position > 1) {
                    page.alpha = 0f
                } else {
                    page.pivotX = if (position < 0f) page.width.toFloat() else 0f
                    page.rotationY = 90f * position
                    page.alpha = 1f
                }
            }

            PageAnimationType.FLIP_HORIZONTAL -> {
                page.cameraDistance = 10000f
                page.pivotX = page.width / 2f
                page.pivotY = page.height / 2f
                page.rotationY = 180f * position
                page.alpha = if (position in -0.5f..0.5f) 1f else 0f
            }
        }
    }

}