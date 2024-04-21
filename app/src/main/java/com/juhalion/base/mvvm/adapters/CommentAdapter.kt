package com.juhalion.base.mvvm.adapters

import com.callscreen.caller.basemvvm.R
import com.callscreen.caller.basemvvm.databinding.LayoutItemCommentBinding
import com.juhalion.base.mvvm.models.response.comments.Comment
import com.juhalion.base.mvvm.ui.base.BaseRecyclerViewAdapter

class CommentAdapter : BaseRecyclerViewAdapter<Comment, LayoutItemCommentBinding>() {
    override fun getLayout(): Int = R.layout.layout_item_comment

    override fun onBindViewHolder(
        holder: Companion.BaseViewHolder<LayoutItemCommentBinding>,
        position: Int,
    ) {
        holder.binding.comment = listData[position]
        holder.binding.tvComment.setOnClickListener {
            listener?.invoke(it, listData[position], position)
        }
    }
}