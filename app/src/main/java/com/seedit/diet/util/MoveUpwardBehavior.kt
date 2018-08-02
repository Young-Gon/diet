package com.seedit.diet.util

import android.content.Context
import android.support.design.widget.AppBarLayout
import android.support.design.widget.CoordinatorLayout
import android.util.AttributeSet
import android.view.View
import com.androidadvance.topsnackbar.TSnackbar


class MoveUpwardBehavior(context: Context,attrs:AttributeSet) : CoordinatorLayout.Behavior<AppBarLayout>(context,attrs)
{
	override fun layoutDependsOn(parent: CoordinatorLayout?, child: AppBarLayout?, dependency: View?): Boolean {
		return dependency is TSnackbar.SnackbarLayout
	}

	override fun onDependentViewChanged(parent: CoordinatorLayout, child: AppBarLayout, dependency: View): Boolean {
		child.translationY =dependency.height+dependency.translationY
		return false
	}
}