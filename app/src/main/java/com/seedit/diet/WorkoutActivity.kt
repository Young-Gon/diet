package com.seedit.diet

import android.Manifest
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gondev.clog.CLog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rpolicante.keyboardnumber.KeyboardNumberPicker
import com.rpolicante.keyboardnumber.KeyboardNumberPickerHandler
import com.seedit.diet.adapter.ArrayListRecyclerViewAdapter
import com.seedit.diet.adapter.SearchViewAdapter
import com.seedit.diet.adapter.ViewBinder
import com.seedit.diet.database.entity.RecommendWorkoutEntity
import com.seedit.diet.database.entity.WorkoutEntity
import com.seedit.diet.database.entity.WorkoutRelationshipEntity
import com.seedit.diet.database.entity.WorkoutWithRecommend
import com.seedit.diet.viewmodel.WorkoutViewModel
import com.seedit.diet.viewmodel.getViewModel
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_workout.*
import kotlinx.android.synthetic.main.alert_request_workout.view.*
import kotlinx.android.synthetic.main.item_insert_food.view.*
import java.util.*

fun Context.startWorkoutActivity(workoutEntity: WorkoutEntity= WorkoutEntity())=
		startActivity(Intent(this,WorkoutActivity::class.java)
				.putExtra(INTENT_KEY_ENTITY,workoutEntity))

fun Context.startWorkoutActivity(createAt: Calendar) =
		startActivity(Intent(this, WorkoutActivity::class.java)
				.putExtra(INTENT_KEY_CREATE_AT, createAt.timeInMillis))

private const val INTENT_KEY_ENTITY = "entity"
private const val INTENT_KEY_CREATE_AT= "createAt"

class WorkoutActivity : AppCompatActivity(), KeyboardNumberPickerHandler
{
	private lateinit var workEntity: WorkoutEntity
	private lateinit var workoutViewModel: WorkoutViewModel
	private lateinit var adapter: ArrayListRecyclerViewAdapter<InsertWorkoutBinder, WorkoutWithRecommend>

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_workout)

		workEntity = intent.getParcelableExtra(INTENT_KEY_ENTITY)?: WorkoutEntity().apply {
			createAt=Date(intent.getLongExtra(INTENT_KEY_CREATE_AT,0))
		}
		workoutViewModel=getViewModel(WorkoutViewModel::class.java)

		// spinner
		editWorkoutName.setText(workEntity.title)

		searchView.setAdapter(SearchViewAdapter(this, R.layout.item_searchview_popup, workoutViewModel))
		searchView.setOnItemClickListener { parent, view, position, id ->
			val item=parent.getItemAtPosition(position) as RecommendWorkoutEntity
			addToList(item)
		}

		updateWorkoutEntity()

		adapter=ArrayListRecyclerViewAdapter(R.layout.item_insert_food,InsertWorkoutBinder::class)
		recyclerView.adapter=adapter
		workoutViewModel.findWorkoutByID(workEntity.id)
		workoutViewModel.observe(this, Observer {it?.let {
			CLog.d("$it")
			//adapter.appendItem(it)
			with(adapter) {
				if (size == 0) {
					addAll(it)
					notifyItemRangeChanged(0, it.size)
				}
			}
		}})
	}

	override fun onDestroy() {
		if(adapter.size==0) {
			if (workEntity.id != 0L)
				workoutViewModel.delete(workEntity)
		}
		else {
			workEntity.title=if(editWorkoutName.text.length==0)"오늘의 운동" else editWorkoutName.text.toString()
			workoutViewModel.insert(workEntity, adapter)
		}

		super.onDestroy()
	}

	private fun calTotalCalorie()
	{
		workEntity.content=""
		workEntity.calorie=0f
		adapter.forEachIndexed{index,item->
			workEntity.content+="${item.recommendWorkoutEntity.name} ${item.relationship.time} 분"
			if(index+1<adapter.size)
				workEntity.content+=", "

			workEntity.calorie+=item.recommendWorkoutEntity.calorie*item.relationship.time/30
		}
		updateWorkoutEntity()
	}

	private fun updateWorkoutEntity() {
		Glide.with(this)
				.load(workEntity.picture)
				.thumbnail(0.1f)
				.apply(RequestOptions()
						.centerCrop()
						.error(R.drawable.exercises_empty))
				.into(imgWorkoutPicture)

		txtTotalCal.text=String.format("총 칼로리: %,dKcal",workEntity.calorie.toInt())
	}

	fun onClickWorkoutImage(view: View) {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
			TedPermission.with(this)
					.setPermissionListener(object : PermissionListener {
						override fun onPermissionGranted() {
							openImagePicker()
						}

						override fun onPermissionDenied(deniedPermissions: java.util.ArrayList<String>?) {
						}
					})
					.setRationaleMessage("프로필 설정을 위해여 갤러리 접근 권한이 필요 합니다")
					.setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
					.check()
			return
		}
		openImagePicker()
	}

	private fun openImagePicker() {
		val bottomSheetDialogFragment = TedBottomPicker.Builder(this)
				.setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
					override fun onImageSelected(uri: Uri) {
						Glide.with(this@WorkoutActivity)
								.load(uri)
								.thumbnail(0.1f)
								.apply (RequestOptions()
										.centerCrop()
										.error(R.drawable.exercises_empty))
								.into(imgWorkoutPicture)

						workEntity.picture=uri
					}
				})
				.create()

		bottomSheetDialogFragment.show(supportFragmentManager)
	}

	fun onClickAddExercise(v: View){
		if(searchView.length()==0)
			return

		//TODO 찾은 운동을 채워 넣읍시다
		val view= LayoutInflater.from(this).inflate(R.layout.alert_request_workout,null)

		AlertDialog.Builder(this@WorkoutActivity)
				.setTitle("운동 추가")
				.setMessage("'"+searchView.text+"' 검색된 운동이 없습니다.\n 해당 운동의 운동량을 추가 입력 하시겠습니까?")
				.setView(view)
				.setPositiveButton(android.R.string.ok) { dialogInterface, i: Int ->
					if(view.editContent.length()==0)
					{
						Toast.makeText(this@WorkoutActivity,"운동 방법을 입력해 주세요",Toast.LENGTH_SHORT).show()
						return@setPositiveButton
					}
					if (view.editCalorie.length() == 0) {
						Toast.makeText(this@WorkoutActivity, "운동량을 입력해 주세요", Toast.LENGTH_SHORT).show()
						return@setPositiveButton
					}
					val item=RecommendWorkoutEntity(searchView.text.toString(),view.editContent.text.toString(),view.editCalorie.text.toString().toFloat(),null)
					workoutViewModel.insert(item)
					addToList(item)
				}
				.setNegativeButton(android.R.string.cancel,null)
				.show()
	}

	private fun addToList(item: RecommendWorkoutEntity) {
		searchView.setText("")
		CLog.d(item.name)
		WorkoutWithRecommend(WorkoutRelationshipEntity(workEntity.id,item.id),item).apply {
			adapter.indexOf(this).let{index->
				if(index==-1) {
					adapter.add(this)
					adapter.notifyItemInserted(adapter.size-1)
				}
				else {
					adapter[index].relationship.time+= 30
					adapter.notifyItemChanged(index)
				}
			}
		}
		calTotalCalorie()
	}

	override fun onConfirmAction(picker: KeyboardNumberPicker?, value: String?) {
		if(!TextUtils.isEmpty(value)){
			(picker?.item as WorkoutWithRecommend).apply {
				CLog.d("size=${adapter.size}")
				relationship.time=value?.toInt()!!
				adapter.notifyItemChanged(adapter.indexOf(this))
			}
			calTotalCalorie()
		}
	}

	override fun onCancelAction(picker: KeyboardNumberPicker?) {
	}

	override fun onDeleteAction(picker: KeyboardNumberPicker?): Unit =with(adapter){
		//workoutViewModel.delete((picker?.item as WorkoutWithRecommend).relationship)
		find {
			it == picker?.item
		}?.let {
			indexOf(it).let {
				removeAt(it)
				notifyItemRemoved(it)
			}
		}
	}

	class InsertWorkoutBinder(itemView: View): ViewBinder<WorkoutWithRecommend>(itemView) {
		override fun bind(item: WorkoutWithRecommend, position: Int)  =with(itemView){
			txtFoodName.text=item.recommendWorkoutEntity.name
			txtNumber.text="${item.relationship.time} 분"
			txtCalorie.text=String.format("%,d Kcal",item.relationship.time*item.recommendWorkoutEntity.calorie.toInt()/30)
		}

		override fun onClick(view: View?) {
			super.onClick(view)

			KeyboardNumberPicker.Builder(10)
					.setTheme(R.style.AppTheme_Dialog)
					.setTitle("운동 시간을 입력 하세요")
					.setItemName(item?.recommendWorkoutEntity?.name)
					.setDefaultValue(item?.relationship?.time.toString())
					.setEnableDeleteButton()
					.create()
					.setItem(item)
					.show((view?.context as AppCompatActivity).supportFragmentManager, "TAG")
		}
	}
}
