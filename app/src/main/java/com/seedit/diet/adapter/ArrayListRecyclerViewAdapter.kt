package com.seedit.diet.adapter

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import kotlin.reflect.KClass

open class ArrayListRecyclerViewAdapter<VH : ViewBinder<ITEM>, ITEM>(
        private var itemList: MutableList<ITEM>,
        @LayoutRes
        private val layoutRes: Int,
        private val vhClass: KClass<VH>
) : RecyclerView.Adapter<VH>(),
        MutableList<ITEM> by itemList {
    constructor(@LayoutRes layoutRes: Int, vhClass: KClass<VH>) : this(mutableListOf(), layoutRes, vhClass)

    override fun getItemCount() = itemList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            vhClass.constructors.first().call(LayoutInflater.from(parent.context).inflate(this.layoutRes, parent, false))

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(itemList[position],position)

    fun appendItem(list: List<ITEM>) {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return itemList.size
            }

            override fun getNewListSize(): Int {
                return list.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return itemList.get(oldItemPosition) === list.get(newItemPosition)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val newProduct:ITEM = list.get(newItemPosition)
                val oldProduct:ITEM = itemList.get(oldItemPosition)
                return newProduct?.equals(oldProduct)?:false
            }
        })
        itemList = list.toMutableList()
        result.dispatchUpdatesTo(this)
    }
}