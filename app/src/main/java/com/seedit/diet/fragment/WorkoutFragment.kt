package com.seedit.diet.fragment

import android.arch.lifecycle.Observer
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.seedit.diet.R
import com.seedit.diet.adapter.ArrayListRecyclerViewAdapter
import com.seedit.diet.adapter.ViewBinder
import com.seedit.diet.database.entity.RecommendWorkoutEntity
import com.seedit.diet.database.entity.WorkoutEntity
import com.seedit.diet.startWorkoutActivity
import com.seedit.diet.viewmodel.RecommendWorkoutRelationshipViewModel
import com.seedit.diet.viewmodel.getViewModel
import kotlinx.android.synthetic.main.fragment_workout.view.*
import kotlinx.android.synthetic.main.item_workout.view.*
import java.util.*

class WorkoutFragment:BaseFragment()
{
	private lateinit var viewModel: RecommendWorkoutRelationshipViewModel
	private lateinit var adapter:ArrayListRecyclerViewAdapter<WorkoutViewBinder, WorkoutEntity>

    override fun getContentLayoutRes()=R.layout.fragment_workout

	private fun bindViewWithData(attachView: View, recommendWorkoutEntity: RecommendWorkoutEntity) = with(attachView) {
		//recommendPicture.setImageResource(recommendWorkoutEntity.imageRes)
		Glide.with(this)
				.load(recommendWorkoutEntity.imageRes)
				.thumbnail(0.1f)
				.into(recommendPicture)

		txtRecommendTitle.text="추천운동 - ${recommendWorkoutEntity.name}"
		txtRecommendContent.text=recommendWorkoutEntity.content
	}

	override fun onContentViewCreated(view: View, calendar: Calendar) {
		if(!::adapter.isInitialized) {
			ArrayListRecyclerViewAdapter(R.layout.item_workout, WorkoutViewBinder::class).let {
				adapter = it
				view.recyclerView.adapter = adapter
			}
		}
		if(!::viewModel.isInitialized) {
			viewModel = getViewModel(RecommendWorkoutRelationshipViewModel::class.java)
			viewModel.observeForRecommend(this, Observer {
				if (it == null || it.isEmpty()) {
					viewModel.insertNewRecommendWorkout(getCurrentCalender())
				} else {
					bindViewWithData(getAttachView(), it[0])
				}
			})
			viewModel.observeForWorkout(this, Observer {
				it?.let {
					adapter.appendItem(it)
				}
			})
		}
		viewModel.find(calendar)

		view.recommendPicture.clipToOutline=true
		view.fab.setOnClickListener { it.context.startWorkoutActivity() }
    }

	class WorkoutViewBinder(containerView: View) : ViewBinder<WorkoutEntity>(containerView) {
		override fun bind(item: WorkoutEntity, position: Int) =with(itemView){
			Glide.with(this)
					.load(item.picture)
					.thumbnail(0.1f)
					.apply(RequestOptions()
							.centerCrop()
							.placeholder(gun0912.tedbottompicker.R.drawable.ic_gallery)
							.error(R.drawable.if_apple))
					.into(imgFood)

			txtCategory.text=item.title
			txtContents.text=item.content
			txtTotalCalorie.text=String.format("총 %,dKcal",item.calorie.toInt())
		}

		override fun onClick(view: View?) {
			super.onClick(view)
			view?.context?.startWorkoutActivity(item!!)
		}
	}
}