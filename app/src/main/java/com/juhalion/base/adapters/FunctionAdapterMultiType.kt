package com.juhalion.base.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.juhalion.bae.base.BaseRecyclerViewAdapter
import com.juhalion.base.R
import com.juhalion.base.databinding.LayoutItemFunctionBinding
import com.juhalion.base.databinding.LayoutItemFunctionHeaderBinding
import com.juhalion.base.databinding.LayoutItemSwitchBinding
import com.juhalion.base.models.function.JFunction

class FunctionAdapterMultiType : BaseRecyclerViewAdapter<JFunction, ViewDataBinding>() {

    override fun getItemViewTypeForPosition(position: Int) = when (listData[position]) {
        is JFunction.HeaderItem -> JFunction.TYPE_HEADER
        is JFunction.FunctionItem -> JFunction.TYPE_FUNCTION
        is JFunction.SwitchItem -> JFunction.TYPE_SWITCH
    }

    override fun getLayout(viewType: Int): Int = when (viewType) {
        JFunction.TYPE_FUNCTION -> R.layout.layout_item_function
        JFunction.TYPE_HEADER -> R.layout.layout_item_function_header
        JFunction.TYPE_SWITCH -> R.layout.layout_item_switch
        else -> throw IllegalArgumentException("Invalid view type")
    }

    override fun createDiffCallback(): DiffUtil.ItemCallback<JFunction> {
        return object : DiffUtil.ItemCallback<JFunction>() {
            override fun areItemsTheSame(
                oldItem: JFunction, newItem: JFunction
            ) = when {
                oldItem is JFunction.HeaderItem && newItem is JFunction.HeaderItem -> oldItem.title == newItem.title
                oldItem is JFunction.FunctionItem && newItem is JFunction.FunctionItem -> oldItem.title == newItem.title
                oldItem is JFunction.SwitchItem && newItem is JFunction.SwitchItem -> oldItem.id == newItem.id
                else -> false
            }

            override fun areContentsTheSame(
                oldItem: JFunction, newItem: JFunction
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun bind(
        binding: ViewDataBinding, item: JFunction, position: Int
    ) {
        when (item) {
            is JFunction.HeaderItem -> {
                (binding as LayoutItemFunctionHeaderBinding).headerItem = item
            }

            is JFunction.FunctionItem -> {
                (binding as LayoutItemFunctionBinding).functionItem = item
                binding.root.setOnClickListener {
                    listener?.invoke(binding.root, item, position)
                }
            }

            is JFunction.SwitchItem -> {
                val switchBinding = (binding as LayoutItemSwitchBinding)
                switchBinding.tvTitle.text = item.title
                switchBinding.switchItem.isChecked = item.isChecked
                switchBinding.switchItem.setOnCheckedChangeListener { _, isChecked ->
                    // Propagate the event to the fragment
                    item.isChecked = isChecked
                    listener?.invoke(switchBinding.switchItem, item, position)
                }
            }
        }
    }
}