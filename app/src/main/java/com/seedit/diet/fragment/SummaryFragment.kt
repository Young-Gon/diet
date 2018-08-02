package com.seedit.diet.fragment

import android.arch.lifecycle.Observer
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.androidadvance.topsnackbar.TSnackbar
import com.gondev.clog.CLog
import com.seedit.diet.MyInfoActivity
import com.seedit.diet.R
import com.seedit.diet.database.entity.BodyEntity
import com.seedit.diet.database.entity.ProfileEntity
import com.seedit.diet.viewmodel.SummaryViewModel
import com.seedit.diet.viewmodel.getViewModel
import kotlinx.android.synthetic.main.alert_request_calorie.view.*
import kotlinx.android.synthetic.main.fragment_summary.view.*
import java.text.SimpleDateFormat
import java.util.*



class SummaryFragment:BaseFragment() {
    private lateinit var summaryViewModel: SummaryViewModel
	private lateinit var profileEntity: ProfileEntity
	private lateinit var bodyEntity: List<BodyEntity>
	private val sdf=SimpleDateFormat("yyyy-MM-dd")

	private var snackbar:TSnackbar?=null

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
					updateSummary(Calendar.getInstance())
			}
		})
		summaryViewModel.findBody(this, Observer {
			if(it==null)
			{
				// 프로필 스넥바가 있을경우 스넥바를 열지 말자
				if(snackbar!=null && !snackbar?.isShown!!)
					showWeightButton()
			}
			else
			{
				bodyEntity=it
				if(::profileEntity.isInitialized)
					updateSummary(Calendar.getInstance())
			}
		})
	}

	override fun onContentViewCreated(view: View, calendar: Calendar) {
		// TODO 날짜 변경시 써머리 변경
		updateSummary(calendar)
    }

	private fun createSnackbar(text: String)=
		TSnackbar.make( getAttachView() as CoordinatorLayout, text, Snackbar.LENGTH_INDEFINITE).apply {
		view.findViewById<TextView>(com.androidadvance.topsnackbar.R.id.snackbar_text).setTextColor(Color.WHITE)
			snackbar=this
	}

	fun showWeightButton() {
		//Snackbar.make(getAttachView(),"",Snackbar.LENGTH_INDEFINITE).
		createSnackbar("오늘의 몸무게를 추가해 주세요.").setAction("몸무게 추가"){
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
						summaryViewModel.insert(sdf.format(getCurrentCalender().timeInMillis),view.editCalorie.text.toString().toInt())
					}
					.setNegativeButton(android.R.string.cancel){dialogInterface, i: Int ->
						showWeightButton()
					}.show()
		}.show()
	}

	fun updateSummary(calendar: Calendar) {
		var today = sdf.format(calendar.timeInMillis)
		CLog.i(today)
		if(!::profileEntity.isInitialized || !::bodyEntity.isInitialized)
		{
			return
		}

		val body = bodyEntity.firstOrNull {
			it.date <= today
		}
		val dday=sdf.format(profileEntity.targetDday)
		val targetWeight=profileEntity.targetWeight
		if (body == null) {
			showWeightButton()
			getAttachView().txtSummaryLeft.text="""D-Day: $dday
							  |몸무게: N/A
							  |BMI: N/A""".trimMargin()
			getAttachView().txtSummaryRight.text="""감량 목표: $targetWeight
							   |현재: N/A
							   |달성율: N/A""".trimMargin()
			return
		}
		if(body.date!=today)
			showWeightButton()
		else
			snackbar?.dismiss()

		val weight=body.weight
		val BMI=(weight.toFloat() /(profileEntity.height.toFloat()*profileEntity.height.toFloat()))* 10000
		val marginWeight=targetWeight-weight
		val weightPercentage=((profileEntity.weight-weight).toFloat()/(profileEntity.weight-targetWeight).toFloat())*100

		getAttachView().txtSummaryLeft.text="""D-Day: $dday
							  |몸무게: $weight
							  |BMI: ${BMI.toInt()}""".trimMargin()
		getAttachView().txtSummaryRight.text="""감량 목표: $targetWeight
							   |현재: $marginWeight
							   |달성율: $weightPercentage""".trimMargin()
	}
}