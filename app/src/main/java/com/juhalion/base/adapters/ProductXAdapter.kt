package com.juhalion.base.adapters

import androidx.recyclerview.widget.DiffUtil
import com.juhalion.bae.base.BaseRecyclerViewAdapter
import com.juhalion.base.R
import com.juhalion.base.databinding.LayoutItemProductxBinding
import com.juhalion.base.models.product.ProductX

class ProductXAdapter : BaseRecyclerViewAdapter<ProductX, LayoutItemProductxBinding>() {
    override fun getLayout(viewType: Int) = R.layout.layout_item_productx

    override fun createDiffCallback(): DiffUtil.ItemCallback<ProductX> {
        return object : DiffUtil.ItemCallback<ProductX>() {
            override fun areItemsTheSame(
                oldItem: ProductX, newItem: ProductX
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: ProductX, newItem: ProductX
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewTypeForPosition(position: Int) = ITEM_DEFAULT_TYPE

    override fun bind(
        binding: LayoutItemProductxBinding, productItem: ProductX, position: Int
    ) {
        binding.apply {
            item = productItem
        }
    }
}