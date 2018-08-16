package com.seedit.diet.adapter

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.extensions.LayoutContainer

abstract class ViewBinder<ITEM>(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer, View.OnClickListener
{
    var item: ITEM? = null
	internal abstract fun bind(item: ITEM, position: Int)

    override fun onClick(view: View?) {
    }
}

fun ViewGroup.inflateLayout(@LayoutRes layoutRes: Int):View = LayoutInflater.from(context)
		.inflate(layoutRes, this, false)