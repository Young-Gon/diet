package com.seedit.diet.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.view.View
import com.gondev.clog.CLog
import com.seedit.diet.R
import com.seedit.diet.adapter.ArrayListRecyclerViewAdapter
import com.seedit.diet.adapter.ViewBinder
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.database.entity.RecommendWithDiet
import com.seedit.diet.database.repository.Repository
import com.seedit.diet.viewmodel.RecommendDietViewModel
import kotlinx.android.synthetic.main.fragment_diet.view.*
import java.util.*

class DietFragment:BaseFragment() {
    private lateinit var recommendWithDiet: RecommendWithDiet
    private lateinit var recommendDietViewModel: RecommendDietViewModel
    private lateinit var adapter:ArrayListRecyclerViewAdapter<DietViewBinder,DietEntity>

    override fun getContentLayoutRes()=R.layout.fragment_diet

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        val calendar=Calendar.getInstance()
        val factory= RecommendDietViewModel.Factory(activity?.application!!, Repository.provideRecommendDataSource(context!!),calendar)
        recommendDietViewModel= ViewModelProviders.of(this,factory).get(RecommendDietViewModel::class.java)
        recommendDietViewModel.observe(this,Observer {
            if(it==null || it.isEmpty())
                recommendWithDiet = recommendDietViewModel.createNewData()
            else
                recommendWithDiet=it[0]

            bindViewWithData(getAttachView(),recommendWithDiet)
        })

        recommendDietViewModel.observable.observe(this, Observer {
            CLog.d("데이터 갱신")
            it?.let { adapter.appendItem(it[0].dietList) }
        })
    }

    private fun bindViewWithData(attachView: View, recommendWithDiet: RecommendWithDiet) {
        attachView.recommendPicture.clipToOutline=true
        attachView.recommendPicture.setImageResource(recommendWithDiet.recommendDiet.dietImageRes)

        attachView.txtRecommendContent.text=recommendWithDiet.recommendDiet.dietContent

        adapter=ArrayListRecyclerViewAdapter(recommendWithDiet.dietList.toMutableList(),R.layout.item_diet,DietViewBinder::class)
        attachView.recyclerView.adapter=adapter
    }

    override fun onContentViewCreated(view: View, calendar: Calendar) {
        recommendDietViewModel.findByDate(calendar)

        view.fab.setOnClickListener {  }
    }

    class DietViewBinder(itemView: View) : ViewBinder<DietEntity>(itemView) {
        override fun bind(item: DietEntity, position: Int) {
        }

    }
}