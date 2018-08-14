package com.seedit.diet.util

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import com.androidadvance.topsnackbar.TSnackbar
import com.gondev.clog.CLog


class ScrollingBehavior(context: Context, attrs:AttributeSet) : AppBarLayout.ScrollingViewBehavior(context,attrs)
{
	override fun layoutDependsOn(parent: CoordinatorLayout?, child: View, dependency: View?)=
		super.layoutDependsOn(parent, child, dependency) || dependency is TSnackbar.SnackbarLayout

	override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
		if(dependency !is TSnackbar.SnackbarLayout)
			return super.onDependentViewChanged(parent, child, dependency)

		child.translationY =dependency.height+dependency.translationY
		var height=parent.height-dependency.height-dependency.translationY
		/*var fl = (height / child.height.toFloat())
		child.scaleY= fl*/
		val layoutParams = child.layoutParams as CoordinatorLayout.LayoutParams
		layoutParams.height= height.toInt()
		child.layoutParams=layoutParams

		CLog.d("parent.height=${parent.height}, child.height=${child.height}, height=$height dependency.height=${dependency.height}, dependency.translationY=${dependency.translationY}")
		return false
	}
}