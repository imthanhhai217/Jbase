package com.juhalion.base.adapters

import androidx.recyclerview.widget.DiffUtil
import com.juhalion.bae.base.BaseRecyclerViewAdapter
import com.juhalion.base.R
import com.juhalion.base.databinding.LayoutItemCommentBinding
import com.juhalion.base.models.comment.Comment

class CommentAdapterSingleType :
    BaseRecyclerViewAdapter<Comment, LayoutItemCommentBinding>() {

    override fun getLayout(viewType: Int) = R.layout.layout_item_comment

    override fun createDiffCallback(): DiffUtil.ItemCallback<Comment> {
        return object : DiffUtil.ItemCallback<Comment>() {
            override fun areItemsTheSame(
                oldItem: Comment, newItem: Comment
            ): Boolean {
                return oldItem.postId == newItem.postId
            }

            override fun areContentsTheSame(
                oldItem: Comment, newItem: Comment
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewTypeForPosition(position: Int) = ITEM_DEFAULT_TYPE

    override fun bind(
        binding: LayoutItemCommentBinding, item: Comment, position: Int
    ) {
        binding.comment = item

        binding.tvComment.setOnClickListener {
            listener?.invoke(it, item, position)
        }
    }
}