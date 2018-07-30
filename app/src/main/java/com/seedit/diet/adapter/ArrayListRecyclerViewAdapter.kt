package com.seedit.diet.adapter

import android.support.annotation.LayoutRes
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.seedit.diet.util.ioThread
import com.seedit.diet.util.mainThread
import kotlin.reflect.KClass

class ArrayListRecyclerViewAdapter<VH : ViewBinder<ITEM>, ITEM>(
        itemList: MutableList<ITEM>,
        @LayoutRes
        private val layoutRes: Int,
        private val vhClass: KClass<VH>
) : RecyclerView.Adapter<VH>(),
        MutableList<ITEM> by itemList
{
    constructor(@LayoutRes layoutRes: Int, vhClass: KClass<VH>) : this(mutableListOf(), layoutRes, vhClass)

    override fun getItemCount() = size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
    LayoutInflater.from(parent.context).inflate(this.layoutRes, parent, false).let {view->
        vhClass.constructors.first().call(view).also {viewHolder->
            view.setOnClickListener(viewHolder)
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) =
        holder.bind(this[position],position).also {
	        holder.item=this[position]
        }

    fun appendItem(list: List<ITEM>)= ioThread {
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return size
            }

            override fun getNewListSize(): Int {
                return list.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return get(oldItemPosition) === list.get(newItemPosition)
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val newProduct = list.get(newItemPosition)
                val oldProduct = get(oldItemPosition)
                return newProduct?.equals(oldProduct)?:false
            }
        })
        clear()
        addAll(list)

        mainThread {
            result.dispatchUpdatesTo(this)
        }
    }
}