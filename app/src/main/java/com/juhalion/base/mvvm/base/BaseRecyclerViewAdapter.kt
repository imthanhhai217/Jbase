package com.juhalion.base.mvvm.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : Any, VBD : ViewDataBinding> :
    RecyclerView.Adapter<BaseRecyclerViewAdapter.BaseViewHolder<VBD>>() {

    private val differ = AsyncListDiffer(
        DifferCallback(),
        AsyncDifferConfig.Builder(createDiffCallback()).build()
    )

    protected val listData: List<T>
        get() = differ.currentList

    fun updateData(data: List<T>) {
        if (data != listData) differ.submitList(data)
    }

    fun clearData() {
        if (listData.isNotEmpty()) differ.submitList(emptyList())
    }

    fun addData(data: List<T>) {
        if (data.isEmpty()) return
        val newList = ArrayList(listData).apply { addAll(data) }
        differ.submitList(newList)
    }

    fun getItem(position: Int): T = listData[position]

    var listener: ((view: View, item: T, position: Int) -> Unit)? = null

    abstract fun getLayout(): Int

    abstract fun createDiffCallback(): DiffUtil.ItemCallback<T>

    override fun getItemCount(): Int = listData.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VBD> {
        val inflater = LayoutInflater.from(parent.context)
        val binding: VBD = DataBindingUtil.inflate(inflater, getLayout(), parent, false)
        return BaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseViewHolder<VBD>, position: Int) {
        bind(holder.binding, getItem(position), position)
    }

    abstract fun bind(binding: VBD, item: T, position: Int)

    private inner class DifferCallback : ListUpdateCallback {
        override fun onInserted(position: Int, count: Int) {
            notifyItemRangeInserted(position, count)
        }

        override fun onRemoved(position: Int, count: Int) {
            notifyItemRangeRemoved(position, count)
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) {
            notifyItemMoved(fromPosition, toPosition)
        }

        override fun onChanged(position: Int, count: Int, payload: Any?) {
            notifyItemRangeChanged(position, count, payload)
        }
    }

    class BaseViewHolder<VBD : ViewDataBinding>(val binding: VBD) :
        RecyclerView.ViewHolder(binding.root)
}