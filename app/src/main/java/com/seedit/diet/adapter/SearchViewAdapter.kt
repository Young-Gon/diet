package com.seedit.diet.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import com.gondev.clog.CLog
import com.seedit.diet.database.entity.SearchViewInfo
import kotlinx.android.synthetic.main.item_searchview_popup.view.*


class SearchViewAdapter<T: SearchViewInfo>(context: Context?, val resource: Int, foodViewModel: DataSet<T>, var dataList: List<T> = listOf()) : ArrayAdapter<T>(context, resource, dataList)
{
	private val listFilter = SearchViewAdapter.ListFilter(this,foodViewModel)

	@SuppressLint("ViewHolder")
	override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
		return LayoutInflater.from(parent?.context).inflate(resource,parent,false).apply {
			getItem(position)?.let {
				txtName.text= it.name
				calorie.text= it.getSubInfo()
			}
		}
	}

	override fun getCount(): Int {
		return dataList.size
	}

	override fun getItem(position: Int): T? {
		return dataList.get(position)
	}

	override fun getFilter(): Filter {
		return listFilter
	}

	private fun publishResults(results: ArrayList<T>, count: Int) {
		dataList = results
		CLog.d(dataList.toString())
		if (count > 0) {
			notifyDataSetChanged()
		} else {
			notifyDataSetInvalidated()
		}
	}

	class ListFilter<T:SearchViewInfo>(val adapter: SearchViewAdapter<T>, val foodViewModel: DataSet<T>) : Filter() {
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
			adapter.publishResults(results.values as ArrayList<T>,results.count)
		}
	}
}

interface DataSet<T:SearchViewInfo> {
	fun findCursor(keyword:String):List<T>
}