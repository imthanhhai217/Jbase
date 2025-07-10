package com.juhalion.bae.view.fragment_tab_view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
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
import com.juhalion.bae.utils.JuExtendFunction.isLive
import com.juhalion.bae.utils.JuExtendFunction.setOnSingleClickListener
import com.juhalion.bae.utils.JuExtendFunction.spToPx
import com.juhalion.bae.utils.JuExtendFunction.updateGuidePercent
import com.juhalion.bae.utils.JuExtendFunction.visible
import com.juhalion.bae.view.fragment_tab_view.animation.PageAnimationType
import com.juhalion.bae.view.fragment_tab_view.animation.PageTransformer

class BottomTabBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {

    private companion object {
        const val TAG = "BottomTabBar"
        const val MAX_BADGE_COUNT = 99
    }

    // Core properties
    private var viewPager: ViewPager2? = null
    private var tabs = emptyList<TabItem>()
    private var currentTab: TabInfo? = null

    // View management - optimized collections
    private val viewMaps = mutableMapOf<TabInfo, LayoutItemBottomNavigationBinding>()
    private val badgeMap = mutableMapOf<TabInfo, Int?>()

    // Callbacks
    var onTabSelecting: ((TabInfo) -> Boolean)? = null
    private var pageChangeCallback: ViewPager2.OnPageChangeCallback? = null

    // Configuration
    private var showTitle = false
    private var showBadge = false
    private var iconHeightPercent = 0.7f
    private var iconScale = 1f
    private var badgePositionX = 0.65f
    private var badgePositionY = 0.05f
    private var badgeBackgroundResId = R.drawable.bg_badge_circle_red
    private var badgeTextColor = Color.WHITE
    private var badgeTextSize = 0f
    private var titleTextSize = 0f
    private var tabPadding = 0

    // Cached resources - lazy initialization for performance
    private val selectedColor by lazy { context.getCompatColor(R.color.tab_selected) }
    private val unselectedColor by lazy { context.getCompatColor(R.color.tab_unselected) }
    private val tabLayoutParams by lazy {
        LayoutParams(0, LayoutParams.MATCH_PARENT).apply { weight = 1f }
    }

    init {
        orientation = HORIZONTAL
        initializeAttributes(attrs)
    }

    /**
     * Initialize attributes - optimized with early validation
     */
    private fun initializeAttributes(attrs: AttributeSet?) {
        if (attrs == null) {
            // Set defaults when no attributes
            badgeTextSize = 12f.spToPx(context)
            titleTextSize = 12f.spToPx(context)
            return
        }

        context.theme.obtainStyledAttributes(attrs, R.styleable.BottomTabBar, 0, 0).apply {
            try {
                showTitle = getBoolean(R.styleable.BottomTabBar_showTitle, false)
                showBadge = getBoolean(R.styleable.BottomTabBar_showBadge, false)

                iconHeightPercent = getFloat(R.styleable.BottomTabBar_iconHeightPercent, 0.7f).coerceIn(0f, 1f)
                iconScale = getFloat(R.styleable.BottomTabBar_iconScale, 1f).coerceIn(0f, 1f)
                badgePositionX = getFloat(R.styleable.BottomTabBar_badgePositionX, 0.65f).coerceIn(0f, 1f)
                badgePositionY = getFloat(R.styleable.BottomTabBar_badgePositionY, 0.05f).coerceIn(0f, 1f)

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

    /**
     * Setup with FragmentActivity - streamlined
     */
    fun setupBottomTabBar(
        viewPager: ViewPager2,
        tabItems: List<TabItem>,
        fragmentActivity: FragmentActivity
    ) {
        require(fragmentActivity.isLive()) { "FragmentActivity is not in a valid state" }
        require(tabItems.isNotEmpty()) { "Tab list must not be empty" }

        // Initialize core properties
        this.viewPager = viewPager
        this.tabs = tabItems
        this.currentTab = null
        clearAllView()

        // Setup ViewPager adapter
        viewPager.adapter = object : FragmentStateAdapter(fragmentActivity) {
            override fun createFragment(position: Int) = tabItems[position].fragment
            override fun getItemCount() = tabItems.size
        }

        // Setup page change callback with memory leak prevention
        pageChangeCallback?.let { viewPager.unregisterOnPageChangeCallback(it) }
        pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                tabItems.getOrNull(position)?.tabInfo?.let { tabInfo ->
                    scrollToTab(tabInfo)
                }
            }
        }.also { viewPager.registerOnPageChangeCallback(it) }

        // Create tab views
        tabItems.forEach { tabItem ->
            val binding = createTabView(tabItem.tabInfo)
            addView(binding.root)
            viewMaps[tabItem.tabInfo] = binding
        }

        // Select first tab
        if (tabs.isNotEmpty()) {
            scrollToTab(tabs.first().tabInfo)
        }
    }

    /**
     * Setup with Fragment
     */
    fun setupBottomTabBar(viewPager: ViewPager2, tabItems: List<TabItem>, fragment: Fragment) {
        setupBottomTabBar(viewPager, tabItems, fragment.requireActivity())
    }

    /**
     * Add animation - simple delegation
     */
    fun transformAnimation(pageAnimationType: PageAnimationType) {
        viewPager?.setPageTransformer(PageTransformer(pageAnimationType))
    }

    /**
     * Update tab selected state - HIGHLY OPTIMIZED
     * Only updates the 2 tabs that need changes
     */
    private fun updateTabSelected(selected: TabInfo) {
        val previousTab = currentTab

        // Update new selected tab
        viewMaps[selected]?.let { binding ->
            binding.tabIcon.setImageResource(selected.iconSelected)
            if (showTitle) binding.tvTitle.setTextColor(selectedColor)
        }

        // Update previous tab if different
        if (previousTab != null && previousTab != selected) {
            viewMaps[previousTab]?.let { binding ->
                binding.tabIcon.setImageResource(previousTab.iconUnselected)
                if (showTitle) binding.tvTitle.setTextColor(unselectedColor)
            }
        }
    }

    /**
     * Update badge - optimized with change detection
     */
    fun updateBadge(tabInfo: TabInfo, count: Int?) {
        if (!showBadge) return

        // Only update if value changed
        if (badgeMap[tabInfo] == count) return

        badgeMap[tabInfo] = count
        viewMaps[tabInfo]?.badgeView?.apply {
            if (count != null && count > 0) {
                visible()
                text = if (count > MAX_BADGE_COUNT) "${MAX_BADGE_COUNT}+" else count.toString()
            } else {
                gone()
            }
        }
    }

    /**
     * Clear badge
     */
    fun clearBadge(tabInfo: TabInfo) {
        if (badgeMap.remove(tabInfo) != null) {
            viewMaps[tabInfo]?.badgeView?.gone()
        }
    }

    /**
     * Clear all badges
     */
    fun clearAllBadges() {
        if (badgeMap.isEmpty()) return
        badgeMap.clear()
        viewMaps.values.forEach { it.badgeView.gone() }
    }

    /**
     * Create tab view - optimized with minimal allocations
     */
    private fun createTabView(itemTag: TabInfo): LayoutItemBottomNavigationBinding {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.layout_item_bottom_navigation, this, false)
            .apply {
                layoutParams = tabLayoutParams // Reuse cached params
                setPadding(tabPadding, tabPadding, tabPadding, tabPadding)
            }

        val binding = LayoutItemBottomNavigationBinding.bind(view)

        // Setup icon
        binding.tabIcon.apply {
            setImageResource(itemTag.iconUnselected)
            scaleX = iconScale
            scaleY = iconScale
        }

        // Setup badge
        if (showBadge) {
            binding.apply {
                guidelineBadgeX.updateGuidePercent(badgePositionX)
                guidelineBadgeY.updateGuidePercent(badgePositionY)
                badgeView.apply {
                    setBackgroundResource(badgeBackgroundResId)
                    setTextColor(badgeTextColor)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, badgeTextSize)
                    gone()
                }
            }
        }

        // Setup title
        binding.tvTitle.apply {
            if (showTitle) {
                visible()
                setText(itemTag.titleRes)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize)
                setTextColor(unselectedColor)
                binding.guidelineIcon.updateGuidePercent(iconHeightPercent)
            } else {
                gone()
                binding.tabIcon.updateLayoutParams<ConstraintLayout.LayoutParams> {
                    bottomToTop = ConstraintLayout.LayoutParams.UNSET
                    bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                }
            }
        }

        // Setup click listener
        binding.root.setOnSingleClickListener { scrollToTab(itemTag) }

        return binding
    }

    /**
     * Scroll to tab - optimized with early returns
     */
    fun scrollToTab(targetTabInfo: TabInfo) {
        // Early return if already selected
        if (currentTab == targetTabInfo) return

        val index = tabs.indexOfFirst { it.tabInfo == targetTabInfo }
        if (index == -1) return

        // Check callback
        if (onTabSelecting?.invoke(targetTabInfo) == false) return

        // Update state
        updateTabSelected(targetTabInfo)
        viewPager?.setCurrentItem(index, true)
        currentTab = targetTabInfo
    }

    /**
     * Scroll to tab by index
     */
    fun scrollToTab(index: Int) {
        tabs.getOrNull(index)?.tabInfo?.let(::scrollToTab) // Safe + concise
    }

    /**
     * Get current tab
     */
    fun getCurrentTab(): TabInfo? = currentTab

    /**
     * Get current tab index
     */
    fun getCurrentTabIndex(): Int = tabs.indexOfFirst { it.tabInfo == currentTab }

    /**
     * Check if tab exists
     */
    fun hasTab(tabInfo: TabInfo): Boolean = tabs.any { it.tabInfo == tabInfo }

    /**
     * Get tab count
     */
    fun getTabCount(): Int = tabs.size

    /**
     * Clear all views - optimized cleanup
     */
    private fun clearAllView() {
        removeAllViews()
        viewMaps.clear()
        badgeMap.clear()
        currentTab = null
    }

    /**
     * Cleanup - prevent memory leaks
     */
    fun cleanup() {
        pageChangeCallback?.let { callback ->
            viewPager?.unregisterOnPageChangeCallback(callback)
        }
        pageChangeCallback = null
        clearAllView()
    }

    /**
     * Auto cleanup on detach
     */
    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        cleanup()
    }
}