package com.seedit.diet

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Filter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.gondev.clog.CLog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.rpolicante.keyboardnumber.KeyboardNumberPicker
import com.rpolicante.keyboardnumber.KeyboardNumberPickerHandler
import com.seedit.diet.adapter.ArrayListRecyclerViewAdapter
import com.seedit.diet.adapter.ViewBinder
import com.seedit.diet.database.entity.*
import com.seedit.diet.viewmodel.FoodViewModel
import com.seedit.diet.viewmodel.viewModel
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.activity_insert_diet.*
import kotlinx.android.synthetic.main.alert_request_calorie.view.*
import kotlinx.android.synthetic.main.item_insert_food.view.*
import kotlinx.android.synthetic.main.item_searchview_popup.view.*


fun Context.startDietActivity(dietEntity: DietEntity=DietEntity()) =
		startActivity(Intent(this, InsertDietActivity::class.java)
				.putExtra(INTENT_KEY_ENTITY, dietEntity))

private const val INTENT_KEY_ENTITY = "entity"

class InsertDietActivity : AppCompatActivity(), KeyboardNumberPickerHandler
{
	lateinit var dietEntity:DietEntity
	lateinit var foodViewModel: FoodViewModel
	lateinit var adapter: ArrayListRecyclerViewAdapter<InsertFoodViewBinder, DietWithFood>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_diet)

	    dietEntity=intent.getParcelableExtra(INTENT_KEY_ENTITY)?:DietEntity()

	    foodViewModel=viewModel(FoodViewModel::class.java)
	    foodViewModel.findDietFoodByDietID(dietEntity.id)
	    foodViewModel.observe(this, Observer {
		    CLog.d("observed ${dietEntity.id}")
		    if (it == null || it.isEmpty()) {
			    if(dietEntity.id!=0L)
				    foodViewModel.findDietFoodByDietID(dietEntity.id)
		    }
		    else
		    {
			    adapter.appendItem(it)

			    dietEntity.calorie=0f
			    dietEntity.content=""
			    it.forEach {
				    dietEntity.calorie+=it.dietFood.foodCount*it.food.calorie
				    dietEntity.content+="${it.food.name} ${it.dietFood.foodCount}개, "
			    }
			    dietEntity.content.let {
				    dietEntity.content=it.removeRange(it.length-2 .. it.length-1)
			    }

			    foodViewModel.insert(dietEntity)
			    updateDietEntity(dietEntity)
		    }
	    })
	    setupHeaderView()

	    updateDietEntity(dietEntity)

	    adapter=ArrayListRecyclerViewAdapter(R.layout.item_insert_food,InsertFoodViewBinder::class)
	    recyclerView.adapter=adapter

	    addFoodEntity.isClickable=false
	    addFoodEntity.isEnabled=false
	    searchView.setAdapter(SearchViewAdapter(this, R.layout.item_searchview_popup, foodViewModel))
	    searchView.setOnItemClickListener { parent, view, position, id ->
		    val item=parent.getItemAtPosition(position) as FoodEntity
		    searchView.setText(item.name)
		    searchView.tag = item
	    }
	    searchView.addTextChangedListener(object : TextWatcher {
		    override fun afterTextChanged(s: Editable?) {
			    if (s?.length!! > 0) {
				    addFoodEntity.isClickable=true
				    addFoodEntity.isEnabled=true
			    }
			    else
			    {
				    addFoodEntity.isClickable=false
				    addFoodEntity.isEnabled=false
			    }
		    }

		    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
		    }

		    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
		    }
	    })
    }

	fun setupHeaderView() {
		Glide.with(this)
				.load(dietEntity.picture)
				.thumbnail(0.1f)
				.apply (RequestOptions()
						.centerCrop()
						.error(R.drawable.if_pomegranate))
				.into(imgFoodPicture)

		//spinner
		spinnerDietCategory.adapter=ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,convertDietCategoryToString())
		spinnerDietCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
			override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
				dietEntity.category=DietCategoryEnum.values()[position]
			}

			override fun onNothingSelected(parent: AdapterView<*>?) {
			}
		}

		spinnerDietCategory.setSelection(dietEntity.category.ordinal)
	}

	fun updateDietEntity(dietEntity: DietEntity) {
		// 화면에 총 칼로리 표시
		txtTotalCal.text=String.format("총 칼로리: %,dKcal",dietEntity.calorie.toInt())
	}


	fun onClickFoodImage(v: View?) {
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

	fun openImagePicker() {
		val bottomSheetDialogFragment = TedBottomPicker.Builder(this)
				.setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
					override fun onImageSelected(uri: Uri) {
						Glide.with(this@InsertDietActivity)
								.load(uri)
								.thumbnail(0.1f)
								.apply (RequestOptions()
										.centerCrop()
										.error(R.drawable.if_pomegranate))
								.into(imgFoodPicture)

						dietEntity.picture=uri
						foodViewModel.insert(dietEntity)
					}
				})
				.create()

		bottomSheetDialogFragment.show(supportFragmentManager)

	}

	fun onClickAddFood(v: View?) = (searchView.tag as FoodEntity?).let {
		if (searchView.length()==0)
			return@let

		if(it==null || it.name != searchView.text.toString())
		{
			val view=LayoutInflater.from(this).inflate(R.layout.alert_request_calorie,null)
			view.foodName.text=searchView.text
			AlertDialog.Builder(this@InsertDietActivity)
				.setTitle("음식 추가")
				.setMessage("검색된 음식이 없습니다.\n 해당 음식의 칼로리를 추가 입력 하시겠습니까?")
				.setView(view)
				.setPositiveButton(android.R.string.ok) { dialogInterface, i: Int ->
					val item=FoodEntity(searchView.text.toString(),view.editCalorie.text.toString().toFloat())
					foodViewModel.insert(item)
					addToList(item)
				}
				.setNegativeButton(android.R.string.cancel,null)
				.show()
			return@let
		}
		addToList(it)
	}

	fun addToList(item: FoodEntity) {
		searchView.setText("")
		foodViewModel.insertDietFoodRelationship(dietEntity,item,1)
	}

	override fun onConfirmAction(numberPicker: KeyboardNumberPicker?, value: String?){
		if(!TextUtils.isEmpty(value))
		{
			CLog.d(value)
			(numberPicker?.item as DietWithFood).dietFood.let {
				it.foodCount = value?.toInt()!!
				foodViewModel.insertDietFoodRelationship(it)
			}
		}
	}

	override fun onCancelAction(numberPicker: KeyboardNumberPicker) {
	}

	override fun onDeleteAction(picker: KeyboardNumberPicker?) {
	}

	class InsertFoodViewBinder(itemView: View) : ViewBinder<DietWithFood>(itemView) {
		override fun bind(item: DietWithFood, position: Int) =with(itemView){
			txtFoodName.text=item.food.name
			txtNumber.text="${item.dietFood.foodCount} 개"
			txtCalorie.text="${item.dietFood.foodCount*item.food.calorie} Kcal"
		}

		override fun onClick(view: View?) {
			super.onClick(view)

			KeyboardNumberPicker.Builder(10)
					.setTheme(R.style.AppTheme_Dialog)
					.setTitle("수량을 입력하세요")
					.setItemName(item?.food?.name)
					.setDefaultValue(item?.dietFood?.foodCount.toString())
					.create()
					.setItem(item)
					.show((view?.context as InsertDietActivity).supportFragmentManager, "TAG")
		}
	}

	class SearchViewAdapter(context: Context?,val resource: Int, foodViewModel: FoodViewModel, var dataList: List<FoodEntity> = listOf()) : ArrayAdapter<FoodEntity>(context, resource, dataList)
	{
		private val listFilter = SearchViewAdapter.ListFilter(this,foodViewModel)

		@SuppressLint("ViewHolder")
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			return LayoutInflater.from(parent?.context).inflate(resource,parent,false).apply {
				getItem(position)?.let {
					txtName.text= it.name
					calorie.text= it.calorie.toString()+" Kcal"
				}
			}
		}

		override fun getCount(): Int {
			return dataList.size
		}

		override fun getItem(position: Int): FoodEntity? {
			return dataList.get(position)
		}

		override fun getFilter(): Filter {
			return listFilter
		}

		private fun publishResults(results: ArrayList<FoodEntity>, count: Int) {
			dataList = results
			CLog.d(dataList.toString())
			if (count > 0) {
				notifyDataSetChanged()
			} else {
				notifyDataSetInvalidated()
			}
		}

		class ListFilter(val adapter: SearchViewAdapter,val foodViewModel: FoodViewModel) : Filter() {
			private val lock = Any()

			override fun performFiltering(prefix: CharSequence?): Filter.FilterResults {
				val results = Filter.FilterResults()

				if (prefix == null || prefix.length == 0) {
					synchronized(lock) {
						results.values = ArrayList<String>()
						results.count = 0
					}
				} else {
					val searchStrLowerCase = prefix.toString().toLowerCase()+"%"

					CLog.d(searchStrLowerCase)
					//Call to database to get matching records using room
					val matchValues = foodViewModel.findCursor(searchStrLowerCase)

					CLog.d(matchValues.toString())
					results.values = matchValues
					results.count = matchValues.size
				}

				return results
			}

			override fun publishResults(constraint: CharSequence?, results: Filter.FilterResults) {
				adapter.publishResults(results.values as ArrayList<FoodEntity>,results.count)
			}
		}
	}
}

