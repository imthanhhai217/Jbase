package com.juhalion.bae.view.fragment_tab_view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.juhalion.bae.R
import com.juhalion.bae.databinding.LayoutItemBottomNavigationBinding
import com.juhalion.bae.utils.JuExtendFunction.getCompatColor
import com.juhalion.bae.utils.JuExtendFunction.gone
import com.juhalion.bae.utils.JuExtendFunction.setOnSingleClickListener
import com.juhalion.bae.utils.JuExtendFunction.spToPx
import com.juhalion.bae.utils.JuExtendFunction.updateGuidePercent
import com.juhalion.bae.utils.JuExtendFunction.visible
import com.juhalion.bae.view.fragment_tab_view.animation.PageAnimationType
import com.juhalion.bae.view.fragment_tab_view.animation.PageTransformer

class BottomTabBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) : LinearLayout(context, attrs) {
    private val TAG = "BottomTabBar"

    private var viewPager: ViewPager2? = null
    private var tabs = emptyList<TabItem>()

    private val viewMaps = mutableMapOf<TabInfo, LayoutItemBottomNavigationBinding>()
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
        context.theme.obtainStyledAttributes(attrs, R.styleable.BottomTabBar, 0, 0).apply {
            try {
                showTitle = getBoolean(R.styleable.BottomTabBar_showTitle, false)
                showBadge = getBoolean(R.styleable.BottomTabBar_showBadge, false)

                iconHeightPercent = getFloat(R.styleable.BottomTabBar_iconHeightPercent, 0.7f).also {
                    if (it !in 0f..1f) Log.w(TAG, "iconHeightPercent must be in 0..1. Using default 0.7f")
                }.coerceIn(0f, 1f)

                iconScale = getFloat(R.styleable.BottomTabBar_iconScale, 1f).also {
                    if (it !in 0f..1f) Log.w(TAG, "iconScale must be in 0..1. Using default 1f")
                }.coerceIn(0f, 1f)

                badgePositionX = getFloat(R.styleable.BottomTabBar_badgePositionX, 0.65f).also {
                    if (it !in 0f..1f) Log.w(TAG, "badgePositionX must be in 0..1. Using default 0.65f")
                }.coerceIn(0f, 1f)

                badgePositionY = getFloat(R.styleable.BottomTabBar_badgePositionY, 0.05f).also {
                    if (it !in 0f..1f) Log.w(TAG, "badgePositionY must be in 0..1. Using default 0.05f")
                }.coerceIn(0f, 1f)

                badgeBackgroundResId = getResourceId(R.styleable.BottomTabBar_badgeBackground, R.drawable.bg_badge_circle_red)

                badgeTextColor = getColor(R.styleable.BottomTabBar_badgeTextColor, Color.WHITE)
                badgeTextSize = getDimension(R.styleable.BottomTabBar_badgeTextSize, 12f.spToPx(context))
                titleTextSize = getDimension(R.styleable.BottomTabBar_titleTextSize, 12f.spToPx(context))

                tabPadding = getDimensionPixelSize(R.styleable.BottomTabBar_tabPadding, 0)
            } finally {
                recycle()
            }
        }
    }

    fun setupBottomTabBar(viewPager: ViewPager2, tabItems: List<TabItem>, fragmentActivity: FragmentActivity) {
        require(tabItems.isNotEmpty()) { "Tab list must not be empty" }

        this.viewPager = viewPager
        this.tabs = tabItems
        this.currentTab = null

        clearAllView()

        viewPager.adapter = object : FragmentStateAdapter(fragmentActivity) {
            override fun createFragment(position: Int) = tabItems[position].fragment

            override fun getItemCount() = tabItems.size
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabItems.getOrNull(position)?.tabInfo?.let {
                    scrollToTab(it)
                }
            }
        })
        tabItems.forEach {
            val binding = createTabView(it.tabInfo)
            addView(binding.root)
            viewMaps[it.tabInfo] = binding
        }
        // Chuyển sang tab đầu tiên
        if (tabs.isNotEmpty()) {
            scrollToTab(tabs.first().tabInfo)
        }
    }

    fun setupBottomTabBar(viewPager: ViewPager2, tabItems: List<TabItem>, fragment: Fragment) {
        setupBottomTabBar(viewPager, tabItems, fragment.requireActivity())
    }

    fun transformAnimation(pageAnimationType: PageAnimationType) {
        viewPager?.setPageTransformer(PageTransformer(pageAnimationType))
    }

    private fun updateTabSelectedState(selected: TabInfo) {
        viewMaps.forEach { (tab, binding) ->
            val selectedState = tab == selected
            binding.tabIcon.setImageResource(if (selectedState) tab.iconSelected else tab.iconUnselected)

            val color = if (selectedState) context.getCompatColor(R.color.tab_selected)
            else context.getCompatColor(R.color.tab_unselected)
            binding.tvTitle.setTextColor(color)
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
            val badge = viewMaps[tab]?.badgeView ?: return@forEach
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

    private fun createTabView(itemTag: TabInfo): LayoutItemBottomNavigationBinding {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_item_bottom_navigation, this, false).apply {
            layoutParams = LayoutParams(0, LayoutParams.MATCH_PARENT).apply {
                weight = 1f
            }
            setPadding(tabPadding, tabPadding, tabPadding, tabPadding)
        }
        val binding = LayoutItemBottomNavigationBinding.bind(view)
        binding.apply {
            tabIcon.apply {
                setImageResource(itemTag.iconUnselected)
                scaleX = iconScale
                scaleY = iconScale
            }

            badgeView.apply {
                if (showBadge) {
                    guidelineBadgeX.updateGuidePercent(badgePositionX)
                    guidelineBadgeY.updateGuidePercent(badgePositionY)

                    setBackgroundResource(badgeBackgroundResId)
                    setTextColor(badgeTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, badgeTextSize)
                } else {
                    Log.w(TAG, "createTabView: Unnecessary set guide badge percent when showBadge is false!")
                }
                gone()
            }

            tvTitle.apply {
                if (showTitle) {
                    visible()
                    setText(itemTag.titleRes)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)

                    guidelineIcon.updateGuidePercent(iconHeightPercent)
                } else {
                    gone()
                    Log.w(TAG,
                        "createTabView: Unnecessary set iconHeightPercent when showTitle is false! If you want change size of icon please use app:iconScale attribute")
                    tabIcon.updateLayoutParams<ConstraintLayout.LayoutParams> {
                        bottomToTop = ConstraintLayout.LayoutParams.UNSET
                        bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                    }
                }
            }

            root.setOnSingleClickListener {
                scrollToTab(itemTag)
            }
        }

        return binding
    }

    private fun currentTabIndex() = tabs.indexOfFirst { it.tabInfo == currentTab }

    fun scrollToTab(targetTabInfo: TabInfo) {
        val index = tabs.indexOfFirst { it.tabInfo == targetTabInfo }
        if (index != -1) {
            if (onTabSelecting?.invoke(targetTabInfo) != false) {
                currentTab = targetTabInfo
                updateTabSelectedState(targetTabInfo)
                viewPager?.setCurrentItem(index, true)
            }
        }
    }

    fun scrollToTab(index: Int) {
        if (index in tabs.indices) {
            val tabInfo = tabs[index].tabInfo
            if (onTabSelecting?.invoke(tabInfo) != false) {
                viewPager?.setCurrentItem(index, true)
            }
        } else {
            Log.w(TAG, "scrollToTab: index $index out of bounds!")
        }
    }

    private fun clearAllView() {
        removeAllViews()
        viewMaps.clear()
        badgeMap.clear()
    }
}