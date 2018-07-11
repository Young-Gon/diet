package com.seedit.diet.fragment

import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seedit.diet.R
import com.seedit.diet.adapter.ArrayListRecyclerViewAdapter
import com.seedit.diet.adapter.ViewBinder
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.database.entity.RecommendDietEntity
import com.seedit.diet.startDietActivity
import com.seedit.diet.viewmodel.RecommendDietRelationshipViewModel
import com.seedit.diet.viewmodel.viewModel
import kotlinx.android.synthetic.main.fragment_diet.view.*
import kotlinx.android.synthetic.main.item_diet.view.*
import java.util.*

class DietFragment:BaseFragment() {
    private lateinit var viewModel: RecommendDietRelationshipViewModel
    private lateinit var adapter:ArrayListRecyclerViewAdapter<DietViewBinder,DietEntity>

    override fun getContentLayoutRes()=R.layout.fragment_diet

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel=viewModel(RecommendDietRelationshipViewModel::class.java)
	    viewModel.find(Calendar.getInstance())
	    viewModel.observeForRecommend(this,android.arch.lifecycle.Observer {
		    // 페이지 변경시 이쪽으로 호출
		    if(it==null || it.isEmpty()) {
			    viewModel.createNewRecommendDiet(this,getCurrentCalender().time)
		    }
		    else
		    {
			    bindViewWithData(getAttachView(),it[0])
		    }
	    })
	    viewModel.observeForDiet(this,android.arch.lifecycle.Observer {it?.let {
		    adapter.appendItem(it)
	    }})
    }

    private fun bindViewWithData(attachView: View, recommendWithDiet: RecommendDietEntity) {
        attachView.recommendPicture.clipToOutline=true
        attachView.recommendPicture.setImageResource(recommendWithDiet.dietImageRes)

        attachView.txtRecommendContent.text=recommendWithDiet.dietContent
    }

    override fun onContentViewCreated(view: View, calendar: Calendar) {
	    view.recommendPicture.clipToOutline=true
        adapter=ArrayListRecyclerViewAdapter(R.layout.item_diet,DietViewBinder::class)
        view.recyclerView.adapter=adapter

        view.fab.setOnClickListener { it.context.startDietActivity() }
    }

    class DietViewBinder(itemView: View) : ViewBinder<DietEntity>(itemView) {
        override fun bind(item: DietEntity, position: Int) =with(itemView){
	        Glide.with(this)
			        .load(item.picture)
			        .thumbnail(0.1f)
			        .apply(RequestOptions()
					        .centerCrop()
					        .placeholder(gun0912.tedbottompicker.R.drawable.ic_gallery)
					        .error(R.drawable.if_apple))
			        .into(imgFood)

	        txtCategory.text=item.category.toString()
	        txtContents.text=item.content
	        txtTotalCalorie.text=String.format("총 %,dKcal",item.calorie.toInt())
        }

	    override fun onClick(view: View?) {
		    super.onClick(view)
		    view?.context?.startDietActivity(item!!)
	    }
    }
}