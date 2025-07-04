package com.juhalion.base.adapters

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.DiffUtil
import com.juhalion.bae.base.BaseRecyclerViewAdapter
import com.juhalion.base.R
import com.juhalion.base.databinding.LayoutItemFunctionBinding
import com.juhalion.base.databinding.LayoutItemFunctionHeaderBinding
import com.juhalion.base.models.function.JFunction

class FunctionAdapterMultiType : BaseRecyclerViewAdapter<JFunction, ViewDataBinding>() {
    companion object {
        const val TYPE_HEADER = 1
        const val TYPE_FUNCTION = 2
    }

    override fun getItemViewTypeForPosition(position: Int) = when (listData[position]) {
        is JFunction.HeaderItem -> TYPE_HEADER
        is JFunction.JFunctionItem -> TYPE_FUNCTION
    }

    override fun getLayout(viewType: Int): Int = when (viewType) {
        TYPE_FUNCTION -> R.layout.layout_item_function
        TYPE_HEADER -> R.layout.layout_item_function_header
        else -> throw IllegalArgumentException("Invalid view type")
    }

    override fun createDiffCallback(): DiffUtil.ItemCallback<JFunction> {
        return object : DiffUtil.ItemCallback<JFunction>() {
            override fun areItemsTheSame(
                oldItem: JFunction, newItem: JFunction
            ) = when {
                oldItem is JFunction.HeaderItem && newItem is JFunction.HeaderItem -> oldItem.title == newItem.title
                oldItem is JFunction.JFunctionItem && newItem is JFunction.JFunctionItem -> oldItem.title == newItem.title
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
        when {
            binding is LayoutItemFunctionHeaderBinding && item is JFunction.HeaderItem -> {
                binding.headerItem = item
            }

            binding is LayoutItemFunctionBinding && item is JFunction.JFunctionItem -> {
                binding.functionItem = item
                binding.root.setOnClickListener {
                    listener?.invoke(binding.root, item, position)
                }
            }
        }
    }
}