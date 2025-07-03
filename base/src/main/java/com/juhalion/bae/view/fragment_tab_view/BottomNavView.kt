package com.juhalion.bae.view.fragment_tab_view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.juhalion.bae.R
import com.juhalion.bae.utils.JuExtendFunction.getCompatColor
import com.juhalion.bae.utils.JuExtendFunction.gone
import com.juhalion.bae.utils.JuExtendFunction.setOnSingleClickListener
import com.juhalion.bae.utils.JuExtendFunction.spToPx
import com.juhalion.bae.utils.JuExtendFunction.updateGuidePercent
import com.juhalion.bae.utils.JuExtendFunction.visible

class BottomNavView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private val TAG = "BottomNavView"

    private var fragmentManager: FragmentManager? = null
    private var containerID: Int = NO_ID
    private var tabs = emptyList<TabItem>()

    private val viewMaps = mutableMapOf<TabInfo, View>()
    private val fragmentMaps = mutableMapOf<TabInfo, Fragment>()
    private var currentTab: TabInfo? = null

    var onTabSelecting: ((TabInfo) -> Boolean)? = null

    private val badgeMap = mutableMapOf<TabInfo, Int?>()
    private var showTitle = false
    private var showBadge = false

    private val defaultIconHeightPercent = 0.7f
    private var iconHeightPercent = defaultIconHeightPercent
    private var iconScale = 1f

    private var badgePositionX = 0.65f
    private var badgePositionY = 0.05f
    private var badgeBackgroundResId = R.drawable.bg_badge_circle_red
    private var badgeTextColor = Color.WHITE
    private var badgeTextSize = 12f.spToPx(context)
    private var titleTextSize = 12f.spToPx(context)
    private var tabPadding: Int = 0

    init {
        orientation = HORIZONTAL
        context.theme.obtainStyledAttributes(attrs, R.styleable.BottomNavView, 0, 0).apply {
            try {
                showTitle = getBoolean(R.styleable.BottomNavView_showTitle, false)
                showBadge = getBoolean(R.styleable.BottomNavView_showBadge, false)

                iconHeightPercent =
                    getFloat(R.styleable.BottomNavView_iconHeightPercent, 0.7f).also {
                        if (it !in 0f..1f) Log.w(TAG, "iconHeightPercent must be in 0..1. Using default 0.7f")
                    }.coerceIn(0f, 1f)

                iconScale = getFloat(R.styleable.BottomNavView_iconScale, 1f).also {
                    if (it !in 0f..1f) Log.w(TAG, "iconScale must be in 0..1. Using default 1f")
                }.coerceIn(0f, 1f)

                badgePositionX = getFloat(R.styleable.BottomNavView_badgePositionX, 0.65f).also {
                    if (it !in 0f..1f) Log.w(TAG, "badgePositionX must be in 0..1. Using default 0.65f")
                }.coerceIn(0f, 1f)

                badgePositionY = getFloat(R.styleable.BottomNavView_badgePositionY, 0.05f).also {
                    if (it !in 0f..1f) Log.w(TAG, "badgePositionY must be in 0..1. Using default 0.05f")
                }.coerceIn(0f, 1f)

                badgeBackgroundResId =
                    getResourceId(R.styleable.BottomNavView_badgeBackground, R.drawable.bg_badge_circle_red)

                badgeTextColor = getColor(R.styleable.BottomNavView_badgeTextColor, Color.WHITE)
                badgeTextSize =
                    getDimension(R.styleable.BottomNavView_badgeTextSize, 12f.spToPx(context))
                titleTextSize =
                    getDimension(R.styleable.BottomNavView_titleTextSize, 12f.spToPx(context))

                tabPadding = getDimensionPixelSize(R.styleable.BottomNavView_tabPadding, 0)
            } finally {
                recycle()
            }
        }
    }

    @SuppressLint("CommitTransaction")
    fun setupTabView(fragmentManager: FragmentManager, containerID: Int, listItem: List<TabItem>) {
        require(containerID != NO_ID) { "Container ID must be valid" }
        require(listItem.isNotEmpty()) { "Tab list must not be empty" }

        val duplicate = listItem.groupBy { it.tabInfo }.filter { it.value.size > 1 }.keys
        require(duplicate.isEmpty()) { "Duplicate TabType detected: $duplicate" }

        this.fragmentManager = fragmentManager
        this.containerID = containerID
        this.tabs = listItem
        this.currentTab = null

        removeOldFragments()
        clearAllView()

        val transaction = fragmentManager.beginTransaction()

        tabs.forEach { tab ->
            val tagName = tab.fragment::class.simpleName
            val view = createTabView(tab.tabInfo)
            addView(view)
            viewMaps[tab.tabInfo] = view

            val fragment = fragmentManager.findFragmentByTag(tagName) ?: tab.fragment
            fragmentMaps[tab.tabInfo] = fragment
            if (!fragment.isAdded) transaction.add(containerID, fragment, tagName)
            transaction.hide(fragment)
        }
        transaction.commitNowAllowingStateLoss()

        // Chuyển sang tab đầu tiên
        if (tabs.isNotEmpty()) switchTo(tabs.first().tabInfo)
    }

    @SuppressLint("CommitTransaction")
    private fun switchTo(tabInfo: TabInfo) {
        fragmentManager?.let { fm ->
            if (tabInfo == currentTab) return
            val target = fragmentMaps[tabInfo] ?: return

            fm.beginTransaction().apply {
                currentTab?.let { fragmentMaps[it]?.let(::hide) }
                show(target)
            }.commitNowAllowingStateLoss()

            currentTab = tabInfo
            updateTabSelectedState(tabInfo)
        }
    }

    private fun removeOldFragments() {
        fragmentManager?.fragments?.forEach { fragment ->
            if (fragment != null && fragment.isAdded) {
                fragmentManager?.beginTransaction()?.remove(fragment)?.commitNowAllowingStateLoss()
            }
        }
    }

    private fun updateTabSelectedState(selected: TabInfo) {
        viewMaps.forEach { (tab, view) ->
            val selectedState = tab == selected
            view.findViewById<ImageView>(R.id.tabIcon)
                ?.setImageResource(if (selectedState) tab.iconSelected else tab.iconUnselected)

            val color = if (selectedState) context.getCompatColor(R.color.tab_selected)
            else context.getCompatColor(R.color.tab_unselected)
            view.findViewById<TextView>(R.id.tvTitle)?.setTextColor(color)
        }
    }


    fun updateBadge(tabInfo: TabInfo, count: Int?) {
        if (!showBadge) {
            Log.w(TAG, "updateBadge: showBadge is disable! Please set android:showBadge = \"true\"")
            return
        }
        badgeMap[tabInfo] = count
        bindBadge(badgeMap)
    }

    fun clearBadge(tabInfo: TabInfo) {
        badgeMap.remove(tabInfo)
        bindBadge(badgeMap)
    }

    private fun bindBadge(badgeMaps: Map<TabInfo, Int?>) {
        if (!showBadge) return
        badgeMaps.forEach { (tab, count) ->
            val badge = viewMaps[tab]?.findViewById<TextView?>(R.id.badgeView) ?: return@forEach
            if (count != null && count > 0) {
                badge.apply {
                    visible()
                    text = count.toString()
                }
            } else {
                badge.gone()
            }
        }
    }

    private fun createTabView(itemTag: TabInfo): View {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.layout_item_bottom_navigation, this, false)
            .apply {
                layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT).apply {
                    weight = 1f
                }
                setPadding(tabPadding, tabPadding, tabPadding, tabPadding)
            }

        val iconView = view.findViewById<ImageView>(R.id.tabIcon)
        val titleView = view.findViewById<TextView>(R.id.tvTitle)
        val badgeView = view.findViewById<TextView?>(R.id.badgeView)
        val iconGuide = view.findViewById<Guideline>(R.id.guidelineIcon)
        val badgeGuideX = view.findViewById<Guideline>(R.id.guidelineBadgeX)
        val badgeGuideY = view.findViewById<Guideline>(R.id.guidelineBadgeY)

        iconView?.apply {
            setImageResource(itemTag.iconUnselected)
            scaleX = iconScale
            scaleY = iconScale
        }

        badgeView?.apply {
            if (showBadge) {
                badgeGuideX?.updateGuidePercent(badgePositionX)
                badgeGuideY?.updateGuidePercent(badgePositionY)

                setBackgroundResource(badgeBackgroundResId)
                setTextColor(badgeTextColor)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, badgeTextSize)
            } else {
                Log.w(TAG, "createTabView: Unnecessary set guide badge percent when showBadge is false!")
            }
            gone()
        }

        titleView?.apply {
            if (showTitle) {
                visible()
                setText(itemTag.titleRes)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)

                iconGuide?.updateGuidePercent(iconHeightPercent)
            } else {
                gone()
                Log.w(TAG, "createTabView: Unnecessary set iconHeightPercent when showTitle is false! If you want change size of icon please use app:iconScale attribute")
                iconView?.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    bottomToTop = ConstraintLayout.LayoutParams.UNSET
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
        }

        view.setOnSingleClickListener {
            if (onTabSelecting?.invoke(itemTag) != false) {
                switchTo(itemTag)
            }
        }

        return view
    }

    private fun clearAllView() {
        removeAllViews()
        viewMaps.clear()
        fragmentMaps.clear()
    }
}