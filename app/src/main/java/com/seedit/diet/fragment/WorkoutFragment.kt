package com.seedit.diet.fragment

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.view.View
import com.seedit.diet.R
import com.seedit.diet.database.entity.RecommendWorkoutEntity
import com.seedit.diet.viewmodel.RecommendWorkoutRelationshipViewModel
import com.seedit.diet.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_diet.view.*
import java.util.*

class WorkoutFragment:BaseFragment()
{
	private lateinit var viewModel: RecommendWorkoutRelationshipViewModel

    override fun getContentLayoutRes()=R.layout.fragment_workout

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)

		viewModel=getViewModel(RecommendWorkoutRelationshipViewModel::class.java)
		viewModel.find(Calendar.getInstance())
		viewModel.observeForRecommend(this,Observer {
			if(it==null || it.isEmpty()) {
				viewModel.insertNewRecommedWorkout()
			}
			else
			{
				bindViewWithData(getAttachView(),it[0])
			}
		})
	}

	private fun bindViewWithData(attachView: View, recommendWorkoutEntity: RecommendWorkoutEntity) = with(attachView) {
		recommendPicture.setImageResource(recommendWorkoutEntity.imageRes)
		txtRecommendTitle.text="추천운동 - ${recommendWorkoutEntity.name}"
		txtRecommendContent.text=recommendWorkoutEntity.content
	}

	override fun onContentViewCreated(view: View, calendar: Calendar) {
		view.recommendPicture.clipToOutline=true
    }
}