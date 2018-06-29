package com.seedit.diet.adapter

import android.support.v7.widget.RecyclerView
import android.view.View

abstract class ViewBinder<ITEM>(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener
{
    internal abstract fun bind(item: ITEM, position: Int)

    override fun onClick(view: View?) {
    }
}