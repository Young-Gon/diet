package com.seedit.diet.fragment

import android.os.Bundle
import android.view.View
import com.seedit.diet.R
import java.util.*

class SummaryFragment:BaseFragment() {
    private lateinit var contentView: View

    override fun getContentLayoutRes()=R.layout.fragment_summary
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        profileViewModel.observable.observe(this,android.arch.lifecycle.Observer {

        })
    }

    override fun onContentViewCreated(view: View, calendar: Calendar) {
        contentView=view
    }
}