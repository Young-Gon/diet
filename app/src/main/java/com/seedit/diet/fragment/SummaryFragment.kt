package com.seedit.diet.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.Observer
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import com.androidadvance.topsnackbar.TSnackbar
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.gondev.clog.CLog
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.seedit.diet.BuildConfig
import com.seedit.diet.MyInfoActivity
import com.seedit.diet.R
import com.seedit.diet.database.entity.BodyEntity
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.viewmodel.SummaryViewModel
import com.seedit.diet.viewmodel.getViewModel
import gun0912.tedbottompicker.TedBottomPicker
import kotlinx.android.synthetic.main.alert_request_calorie.view.*
import kotlinx.android.synthetic.main.fragment_summary.*
import kotlinx.android.synthetic.main.fragment_summary.view.*
import java.text.SimpleDateFormat
import java.util.*

class SummaryFragment:BaseFragment() {
    private lateinit var summaryViewModel: SummaryViewModel
	private var profileEntity: ProfileEntity?=null
	private lateinit var bodyEntity: List<BodyEntity>
	private val sdf=SimpleDateFormat("yyyy-MM-dd")

	private var snackbar:TSnackbar?=null
	private var todayDiet:Float?=null
	private var todayWorkout: Float?=null
	private var body:BodyEntity?=null

	override fun getContentLayoutRes()=R.layout.fragment_summary

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		summaryViewModel = getViewModel(SummaryViewModel::class.java)
		summaryViewModel.findProfile(this,Observer{
			if (it == null) {
				createSnackbar("프로필을 입력 해 주세요.").setAction("프로필 입력"){
					startActivity(Intent(context, MyInfoActivity::class.java))
				}.show()
			} else {
				profileEntity=it
				if(::bodyEntity.isInitialized)
					updateSummary(getCurrentCalender())

				setChartData(getAttachView().findViewById(R.id.chartDiet),profileEntity?.targetDiet?.toFloat()?:0f,todayDiet?:0f)
				setChartData(getAttachView().findViewById(R.id.chartWorkout),profileEntity?.targetWorkout?.toFloat()?:0f,todayWorkout?:0f)
			}
		})
		summaryViewModel.findBody(this, Observer {
			if(it==null)
			{
				// 프로필 스넥바가 있을경우 스넥바를 열지 말자
				if(snackbar==null || !snackbar?.isShown!!)
					showWeightButton()
			}
			else
			{
				bodyEntity=it
				if(profileEntity!=null)
					updateSummary(getCurrentCalender())
			}
		})
		if(BuildConfig.DEBUG)
			chartDiet.isLogEnabled=true

		summaryViewModel.findDiet(Calendar.getInstance())
		summaryViewModel.observeForDiet(this, Observer {
			it?.let {dietValue->
				// TODO 식사량 차트 만들기~
				CLog.v("dietValue=$dietValue")
				todayDiet=dietValue
			}
			setChartData(getAttachView().findViewById(R.id.chartDiet),profileEntity?.targetDiet?.toFloat()?:0f,todayDiet?:0f)
		})
		summaryViewModel.findWorkout(Calendar.getInstance())
		summaryViewModel.observeForWorkout(this, Observer {
			it?.let {workoutValue->
				CLog.v("workoutValue=$workoutValue")
				todayWorkout=workoutValue
			}
			setChartData(getAttachView().findViewById(R.id.chartWorkout),profileEntity?.targetWorkout?.toFloat()?:0f,todayWorkout?:0f)
		})
	}

	@SuppressLint("ResourceType")
	private fun setChartLegend(@LayoutRes chart: Int):BarChart = with(getAttachView().findViewById(chart) as BarChart){
		xAxis.valueFormatter= IAxisValueFormatter { value, axis ->
			if (value == 0f)
				"목표량"
			else
				"달성량"
		}
		legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
		legend.orientation=Legend.LegendOrientation.HORIZONTAL
		legend.formSize=0f
		legend.textSize=16f

		this
	}

	private fun setChartData(chart: BarChart, data1: Float, data2: Float) =with(chart){
		if (data == null) {
			setDrawGridBackground(false)
			setDrawBarShadow(false)
			setDrawValueAboveBar(true)
			setPinchZoom(false)
			setScaleEnabled(false)
			setDoubleTapToZoomEnabled(false)

			description.isEnabled = false

			xAxis.position = XAxis.XAxisPosition.BOTTOM
			xAxis.setDrawGridLines(false)
			xAxis.granularity = 1f // only intervals of 1 day
			xAxis.labelCount = 7

			axisLeft.axisMinimum = 0f // this replaces setStartAtZero(true)
			axisLeft.spaceTop = 15f
			axisLeft.setLabelCount(8, false)
			axisLeft.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)

			axisRight.setLabelCount(8, false)
			axisRight.axisMinimum = 0f // this replaces setStartAtZero(true)
			axisRight.spaceTop = 15f
			axisRight.setDrawLabels(false)
			axisRight.setDrawLimitLinesBehindData(false)
			axisRight.setDrawGridLines(false)

			val label=when (chart.id) {
				R.id.chartDiet->"식사량"
				R.id.chartWorkout->"운동량"
				R.id.chartWater->"수분섭취량"
				R.id.chartweight->"몸무게"
				else -> throw IllegalArgumentException("리스트에 없는 라벨입니다")
			}

			data = BarData(BarDataSet(listOf(BarEntry(0f, data1), BarEntry(1f, data2)), label).apply {
				val startColor1 = ContextCompat.getColor(context!!, android.R.color.holo_orange_light)
				val startColor2 = ContextCompat.getColor(context!!, android.R.color.holo_blue_light)
				setColors(startColor1, startColor2)
			}).apply {
				setValueTextSize(10f)
				barWidth = 0.9f
			}

			invalidate()
		}
		else
		{
			data.getDataSetByIndex(0).getEntryForIndex(0).y=data1
			data.getDataSetByIndex(0).getEntryForIndex(1).y=data2

			axisLeft.axisMaximum = (if(data1>data2)data1 else data2)*1.1f

			data.notifyDataChanged()
			notifyDataSetChanged()
			invalidate()
		}
	}

	@SuppressLint("ResourceType")
	override fun onContentViewCreated(view: View, calendar: Calendar) {
		// TODO 날짜 변경시 써머리 변경
		view.nestedScrollView.isNestedScrollingEnabled=true
		updateSummary(calendar)

		setChartLegend(R.id.chartDiet).setOnClickListener {
			(activity as OnFragmentInteractionListener).setCurrentPage(1)
		}
		setChartLegend(R.id.chartWorkout).setOnClickListener {
			(activity as OnFragmentInteractionListener).setCurrentPage(2)

		}
		setChartLegend(R.id.chartWater).setOnClickListener {
			val alertView= LayoutInflater.from(context).inflate(R.layout.alert_request_calorie,null)
			alertView.txtUnit.text="ml"
			AlertDialog.Builder(this.context!!)
					.setTitle("수분 섭취랑")
					.setMessage("오늘의 수분 섭취량을 추가해 주세요.")
					.setView(alertView)
					.setPositiveButton(android.R.string.ok) { dialogInterface, i: Int ->
						if(alertView.editCalorie.length()==0) {
							return@setPositiveButton
						}

						//summaryViewModel.insertWater(sdf.format(getCurrentCalender().timeInMillis),v.editCalorie.text.toString().toInt())
						body?.water=alertView.editCalorie.text.toString().toInt()
						summaryViewModel.insertBody(body!!)
					}.show()
		}
		setChartLegend(R.id.chartweight).setOnClickListener(onClickWeightListener)

		val lineChart=getAttachView().findViewById<LineChart>(R.id.chartWeightLine)
		lineChart.xAxis.granularity=1f
		lineChart.xAxis.labelCount=5

		lineChart.xAxis.setValueFormatter { value, axis ->
			CLog.d(sdf.format(Date(value.toLong())))
			sdf.format(Date(value.toLong()))
		}
		lineChart.legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
		lineChart.legend.orientation=Legend.LegendOrientation.HORIZONTAL
		lineChart.legend.formSize=0f
		lineChart.legend.textSize=16f

		if(::summaryViewModel.isInitialized) {
			summaryViewModel.findDiet(calendar)
			summaryViewModel.findWorkout(calendar)
		}
    }

	val onClickWeightListener=View.OnClickListener {
		val view= LayoutInflater.from(context).inflate(R.layout.alert_request_calorie,null)
		view.txtUnit.text="Kg"
		AlertDialog.Builder(this.context!!)
				.setTitle("몸무게 추가")
				.setMessage("오늘의 몸무게를 추가해 주세요.")
				.setView(view)
				.setPositiveButton(android.R.string.ok) { dialogInterface, i: Int ->
					if(view.editCalorie.length()==0) {
						showWeightButton()
						return@setPositiveButton
					}

					//입력된 몸무게를 디비에 추가
					//summaryViewModel.insertWeight(sdf.format(getCurrentCalender().timeInMillis),view.editCalorie.text.toString().toInt())
					body?.weight=view.editCalorie.text.toString().toInt()
					body?.flagWrittenWeight=true
					summaryViewModel.insertBody(body!!)
				}
				.setNegativeButton(android.R.string.cancel){dialogInterface, i: Int ->
					showWeightButton()
				}.show()
	}

	private fun createSnackbar(text: String)=
		TSnackbar.make( getAttachView() as CoordinatorLayout, text, Snackbar.LENGTH_INDEFINITE).apply {
		view.findViewById<TextView>(com.androidadvance.topsnackbar.R.id.snackbar_text).setTextColor(Color.WHITE)
			snackbar=this
	}

	fun showWeightButton() {
		createSnackbar("오늘의 몸무게를 추가해 주세요.").setAction("몸무게 추가",onClickWeightListener).show()
	}

	fun updateSummary(calendar: Calendar) {
		val today = sdf.format(calendar.timeInMillis)
		if(profileEntity==null || !::bodyEntity.isInitialized)
			return

		//월간 몸무게 변화량
		val values=bodyEntity.mapIndexed { index, bodyEntity ->
			Entry(sdf.parse(bodyEntity.date).time.toFloat(), bodyEntity.weight.toFloat())
			//Entry(index.toFloat(), bodyEntity.weight.toFloat())
		}

		if (values.size > 0) {
			val lineChart=getAttachView().findViewById<LineChart>(R.id.chartWeightLine)

			if (lineChart.data != null && lineChart.data.dataSetCount > 0) {
				(lineChart.data.getDataSetByIndex(0) as LineDataSet).values=values
				lineChart.data.notifyDataChanged()
				lineChart.notifyDataSetChanged()
			} else {
				lineChart.data= LineData(listOf(LineDataSet(values, "몸무게 변화량").apply {
					color = ContextCompat.getColor(context!!, android.R.color.holo_blue_light)
					lineWidth = 2f
					setDrawCircles(false)
					valueTextSize = 9f
					setDrawFilled(false)
				}))
			}
			lineChart.invalidate()
		}

		val body = bodyEntity.findLast {
			it.date == today
		} ?: BodyEntity(today, bodyEntity.lastOrNull {
			it.date < today
		}?.weight?:profileEntity?.weight?:0, 0, null)

		val imgNoonBody = getAttachView().findViewById<ImageButton>(R.id.imgNoonbody)

		Glide.with(context!!)
				.load(body.image)
				.thumbnail(0.1f)
				.apply (RequestOptions()
						.centerCrop()
						.error(R.drawable.waist))
				.into(imgNoonBody)

		imgNoonBody.clipToOutline=true
		imgNoonBody.setOnClickListener {
			if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
				TedPermission.with(context!!)
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
				return@setOnClickListener
			}

			openImagePicker()
		}

		this.body=body
		val dday=sdf.format(profileEntity!!.targetDday)
		val targetWeight=profileEntity!!.targetWeight
		setChartData(getAttachView().findViewById(R.id.chartWater),profileEntity?.targetWater?.toFloat()?:0f,body.water.toFloat())
		setChartData(getAttachView().findViewById(R.id.chartweight),profileEntity?.targetWeight?.toFloat()?:0f,body.weight.toFloat())
		if(body.flagWrittenWeight==false)
			showWeightButton()

		if (body.weight==0) {
			getAttachView().txtSummaryLeft.text="""D-Day: $dday
							  |몸무게: N/A
							  |BMI: N/A""".trimMargin()
			getAttachView().txtSummaryRight.text="""감량 목표: $targetWeight
							   |현재: N/A
							   |달성율: N/A""".trimMargin()
			return
		}

		val BMI=(body.weight.toFloat() /(profileEntity!!.height.toFloat()*profileEntity!!.height.toFloat()))* 10000
		val marginWeight=targetWeight-body.weight
		val weightPercentage=((profileEntity!!.weight-body.weight).toFloat()/(profileEntity!!.weight-targetWeight).toFloat())*100

		getAttachView().txtSummaryLeft.text="""D-Day: $dday
							  |몸무게: ${body.weight} Kg
							  |BMI: ${BMI.toInt()}""".trimMargin()
		getAttachView().txtSummaryRight.text="""감량 목표: $targetWeight Kg
							   |현재: $marginWeight Kg
							   |달성율: ${weightPercentage.format(2)}%""".trimMargin()
	}

	fun openImagePicker() {
		val bottomSheetDialogFragment = TedBottomPicker.Builder(context!!)
				.setOnImageSelectedListener(object : TedBottomPicker.OnImageSelectedListener {
					override fun onImageSelected(uri: Uri) {
						body?.image=uri
						summaryViewModel.insertBody(body!!)
						//TODO 이미지 디비에 저장
					}
				})
				.create()

		bottomSheetDialogFragment.show(childFragmentManager)
	}
}

fun Float.format(digits: Int) = java.lang.String.format("%.${digits}f", this)