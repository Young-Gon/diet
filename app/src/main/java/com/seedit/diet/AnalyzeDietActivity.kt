package com.seedit.diet

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.gondev.clog.CLog
import com.seedit.diet.viewmodel.AnalyzeDietViewModel
import com.seedit.diet.viewmodel.getViewModel
import kotlinx.android.synthetic.main.activity_analyze_diet.*
import java.text.SimpleDateFormat
import java.util.*

class AnalyzeDietActivity : AppCompatActivity()
{
	private val sdf= SimpleDateFormat("yyyy-MM-dd")
	private lateinit var viewModel:AnalyzeDietViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_analyze_diet)

		viewModel=getViewModel(AnalyzeDietViewModel::class.java)

		setChart(chartCalorieLine)
		viewModel.findDiet(this, Observer {list->
			if(list==null || list.isEmpty())
				return@Observer

			setChartData(chartCalorieLine,"섭취 칼로리 변화",list.map { entity->
				Entry(entity.createAt,entity.calorie)
			})
		})

		setChart(chartWorkoutLine)
		viewModel.findWorkout(this, Observer {list->
			if(list==null || list.isEmpty())
				return@Observer

			setChartData(chartWorkoutLine, "운동량 변화", list.map { entity->
				Entry(entity.createAt,entity.calorie)
			})
		})

		setChart(chartTotalCalorieLine)
		viewModel.findCalorieDietWorkout(this, Observer {list->
			if(list==null || list.isEmpty())
				return@Observer

			list.forEach {
				CLog.d("${it.calorie}, ${sdf.format(it.createAt)}")
			}
			setChartData(chartTotalCalorieLine,"축적 칼로리",list.mapIndexed { index, entry->
				var entryY=0f
				for(i in 0..index){
					entryY+=list[i].calorie
				}

				Entry(entry.createAt,entryY)
			})
		})

		setChart(chartWeightCalorieLine)
		viewModel.findBody(this, Observer {list->
			if(list==null || list.isEmpty())
				return@Observer

			setChartData(chartWeightCalorieLine, "몸무게 변화", list.map { entity->
				Entry(sdf.parse(entity.date).time.toFloat(), entity.weight.toFloat())
			})
		})

		setChart(chartBMICalorieLine)
		viewModel.findBMI(sdf){ list->
			if(list.isEmpty())
				return@findBMI

			setChartData(chartBMICalorieLine,"BMI 지수 변화",list)
		}
	}

	private fun setChart(lineChart: LineChart) = with(lineChart) {
		xAxis.granularity=1f
		xAxis.labelCount=5
		xAxis.setValueFormatter { value, axis ->
			sdf.format(Date(value.toLong()))
		}
		legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
		legend.orientation= Legend.LegendOrientation.HORIZONTAL
		legend.formSize=0f
		legend.textSize=16f

		axisLeft.axisMinimum = 0f // this replaces setStartAtZero(true)
		axisRight.axisMinimum = 0f // this replaces setStartAtZero(true)
	}

	private fun setChartData(chart: LineChart, title: String, values: List<Entry>) =with(chart) {
		CLog.d(title)
		if (data != null && data.dataSetCount > 0) {
			(data.getDataSetByIndex(0) as LineDataSet).values=values
			data.notifyDataChanged()
			notifyDataSetChanged()
		} else {
			data= LineData(listOf(LineDataSet(values,title).apply {
				color = ContextCompat.getColor(context!!, android.R.color.holo_blue_light)
				lineWidth = 2f
				setDrawCircles(false)
				valueTextSize = 9f
				setDrawFilled(false)
			}))
		}
		invalidate()
	}
}
