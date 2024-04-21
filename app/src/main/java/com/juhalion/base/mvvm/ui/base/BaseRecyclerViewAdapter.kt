package com.juhalion.base.mvvm.ui.base

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T : Any, VBD : ViewDataBinding> :
    RecyclerView.Adapter<BaseRecyclerViewAdapter.Companion.BaseViewHolder<VBD>>() {
    var listData = mutableListOf<T>()

    fun updateData(data: List<T>) {
        this.listData = data as MutableList<T>
        notifyDataSetChanged()
    }

    var listener: ((view: View, item: T, position: Int) -> Unit)? = null

    abstract fun getLayout(): Int

    override fun getItemCount(): Int {
        if (listData.isEmpty()) return 0
        return listData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<VBD> =
        BaseViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), getLayout(), parent, false
            )
        )

    companion object {
        class BaseViewHolder<VBD : ViewDataBinding>(val binding: VBD) :RecyclerView.ViewHolder(binding.root)
    }
}