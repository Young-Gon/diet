package com.seedit.diet.fragment

import android.os.Bundle
import android.view.View
import com.seedit.diet.R
import com.seedit.diet.adapter.ArrayListRecyclerViewAdapter
import com.seedit.diet.adapter.ViewBinder
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.database.entity.RecommendWithDiet
import com.seedit.diet.startDietActivity
import com.seedit.diet.viewmodel.RecommendDietRelationshipViewModel
import com.seedit.diet.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_diet.view.*
import java.util.*

class DietFragment:BaseFragment() {
    private lateinit var viewModel: RecommendDietRelationshipViewModel
    private lateinit var adapter:ArrayListRecyclerViewAdapter<DietViewBinder,DietEntity>

    override fun getContentLayoutRes()=R.layout.fragment_diet

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel=viewModel(RecommendDietRelationshipViewModel::class.java)
	    viewModel.find(Calendar.getInstance())
	    viewModel.setPageChangeListenerForRecommend(this,android.arch.lifecycle.Observer {
		    // 페이지 변경시 이쪽으로 호출
		    if(it==null || it.isEmpty()) {
			    viewModel.createNewRecommendDiet(this,getCurrentCalender().time)
		    }
		    else
		    {
			    bindViewWithData(getAttachView(),it[0])
		    }
	    })
	    viewModel.setPageChangeListenerForDiet(this,android.arch.lifecycle.Observer {
		    // 데이터 변경시 이쪽으로 호출
		    if(::adapter.isInitialized)
		    {
			    adapter.clear()
			    if (it != null && it.isNotEmpty()) {
				    adapter.addAll(it)
				    adapter.notifyDataSetChanged()
			    }
		    }
	    })
	    viewModel.dietObserve(this,android.arch.lifecycle.Observer {
		    if (it != null && it.isNotEmpty()) {
			    if(::adapter.isInitialized)
				    adapter.appendItem(it)
		    }
	    })
    }

    private fun bindViewWithData(attachView: View, recommendWithDiet: RecommendWithDiet) {
        attachView.recommendPicture.clipToOutline=true
        attachView.recommendPicture.setImageResource(recommendWithDiet.recommendDiet.dietImageRes)

        attachView.txtRecommendContent.text=recommendWithDiet.recommendDiet.dietContent
    }

    override fun onContentViewCreated(view: View, calendar: Calendar) {
	    if(::viewModel.isInitialized)
	        viewModel.find(calendar)

	    view.recommendPicture.clipToOutline=true
        adapter=ArrayListRecyclerViewAdapter(R.layout.item_diet,DietViewBinder::class)
        view.recyclerView.adapter=adapter

        view.fab.setOnClickListener { it.context.startDietActivity() }
    }

    class DietViewBinder(itemView: View) : ViewBinder<DietEntity>(itemView) {
        override fun bind(item: DietEntity, position: Int) {
        }
    }
}