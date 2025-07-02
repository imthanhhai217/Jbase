package com.juhalion.base.mvvm.base.fragment_tab_view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.juhalion.base.R
import com.juhalion.base.mvvm.utils.JuExtendFunction.getCompatColor
import com.juhalion.base.mvvm.utils.JuExtendFunction.gone
import com.juhalion.base.mvvm.utils.JuExtendFunction.visible

class FragmentTabView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    LinearLayout(context, attrs) {
    private var fragmentManager: FragmentManager? = null
    private var containerID: Int = NO_ID
    private var tabs = emptyList<TabItem>()

    private val viewMaps = mutableMapOf<TabType, View>()
    private val fragmentMaps = mutableMapOf<TabType, Fragment>()

    private var currentTab: TabType? = null

    /*
    * Return true để cho phép switch tab
    * Return false để block switch tab
    * */
    var onTabSelecting: ((TabType) -> Boolean)? = null

    private val _badgeMap = mutableMapOf<TabType, Int?>()
    private var _showTitle: Boolean = false
    private var _showBadge: Boolean = false

    init {
        orientation = HORIZONTAL
        context.theme.obtainStyledAttributes(attrs, R.styleable.FragmentTabView, 0, 0).apply {
            try {
                _showTitle = getBoolean(R.styleable.FragmentTabView_showTitle, false)
                _showBadge = getBoolean(R.styleable.FragmentTabView_showBadge, false)
            } finally {
                recycle()
            }
        }
    }

    @SuppressLint("CommitTransaction")
    fun setupTabView(fragmentManager: FragmentManager, containerID: Int, listItem: List<TabItem>) {
        require(containerID != NO_ID) { "Container ID must be valid" }
        require(listItem.isNotEmpty()) { "Tab list must not be empty" }

        val duplicate = listItem.groupBy { it.tag }.filter { it.value.size > 1 }.keys
        require(duplicate.isEmpty()) { "Duplicate TabType detected: $duplicate" }

        this.fragmentManager = fragmentManager
        this.containerID = containerID
        this.tabs = listItem

        clearAllView()

        val transaction = fragmentManager.beginTransaction()

        tabs.forEach { tab ->
            val tabName = tab.fragment::class.simpleName
            val view = createTabView(tab.tag)
            addView(view)
            viewMaps[tab.tag] = view

            // Check fragment by tag
            val existingFragment = fragmentManager.findFragmentByTag(tabName)
            val fragment = existingFragment ?: tab.fragment
            fragmentMaps[tab.tag] = fragment

            // Only add if not already added
            if (!fragment.isAdded) {
                transaction.add(containerID, fragment, tabName)
            }

            transaction.hide(fragment)
        }

        transaction.commitNowAllowingStateLoss()

        // Chuyển sang tab đầu tiên
        if (tabs.isNotEmpty()) switchTo(tabs.first().tag)
    }

    @SuppressLint("CommitTransaction")
    private fun switchTo(tabType: TabType) {
        val fm = this.fragmentManager
        if (fm == null) return
        val targetFragment = fragmentMaps[tabType] ?: return
        if (tabType == currentTab) return

        fm.beginTransaction().apply {
            currentTab?.let {
                fragmentMaps[it]?.let { currentFragment ->
                    hide(currentFragment)
                }
            }
            show(targetFragment)
        }.commitNowAllowingStateLoss()

        currentTab = tabType
        updateTabSelectedState(tabType)
    }

    private fun updateTabSelectedState(selectedTab: TabType) {
        viewMaps.forEach { (tab, view) ->
            val isSelected = tab == selectedTab
            view.findViewById<ImageView?>(R.id.tabIcon)
                ?.setImageResource(if (isSelected) tab.iconSelected else tab.iconUnselected)

            val titleColor =
                if (isSelected) context.getCompatColor(R.color.tab_selected) else context.getCompatColor(R.color.tab_unselected)
            view.findViewById<TextView?>(R.id.tvTitle)?.setTextColor(titleColor)
        }
    }

    fun updateBadge(tag: TabType, count: Int?) {
        require(_showBadge) { "showBadge is disable! Please set android:showBadge = \"true\"" }
        _badgeMap[tag] = count
        bindBadge(_badgeMap)
    }

    fun clearBadge(tag: TabType) {
        _badgeMap.remove(tag)
        bindBadge(_badgeMap)
    }

    private fun bindBadge(badgeMaps: Map<TabType, Int?>) {
        val isEnableShowBadge = _showBadge
        if (!isEnableShowBadge) {
            return
        }
        badgeMaps.forEach { (tab, count) ->
            val view = viewMaps[tab] ?: return@forEach
            val badge = view.findViewById<TextView?>(R.id.badgeView) ?: return@forEach
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

    private fun createTabView(itemTag: TabType): View {
        val view = inflate(context, R.layout.layout_item_bottom_navigation, null)

        val icon = view.findViewById<ImageView?>(R.id.tabIcon)
        icon?.setImageResource(itemTag.iconUnselected)

        val tvTitle = view.findViewById<TextView?>(R.id.tvTitle)
        if (_showTitle) {
            tvTitle?.visible()
            tvTitle?.setText(itemTag.titleRes)
        } else {
            tvTitle?.gone()
        }

        // Luôn ẩn badge khi vừa tạo view
        val badgeView = view.findViewById<TextView?>(R.id.badgeView)
        badgeView.gone()

        view.setOnClickListener {
            if (onTabSelecting?.invoke(itemTag) != false) switchTo(itemTag)
        }

        //Mỗi item có weight = 1 để chia đểu về width trên LinearLayout
        val layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
            weight = 1f
        }

        view.layoutParams = layoutParams
        return view
    }

    private fun clearAllView() {
        removeAllViews()
        viewMaps.clear()
        fragmentMaps.clear()
    }
}