package com.seedit.diet

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.support.v4.widget.CursorAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gondev.clog.CLog
import com.seedit.diet.database.entity.DietEntity
import com.seedit.diet.util.ioThread
import com.seedit.diet.util.mainThread
import com.seedit.diet.viewmodel.FoodViewModel
import com.seedit.diet.viewmodel.viewModel
import kotlinx.android.synthetic.main.activity_insert_diet.*
import kotlinx.android.synthetic.main.item_searchview_popup.view.*





fun Context.startDietActivity(dietEntity: DietEntity=DietEntity()) =
		startActivity(Intent(this, InsertDietActivity::class.java)
				.putExtra(INTENT_KEY_ENTITY, dietEntity))

private const val INTENT_KEY_ENTITY = "entity"

class InsertDietActivity : AppCompatActivity() {
	lateinit var originalEntity:DietEntity
	lateinit var dietEntity:DietEntity
	lateinit var foodViewModel: FoodViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert_diet)

	    dietEntity=intent.getParcelableExtra(INTENT_KEY_ENTITY)?:DietEntity()
	    originalEntity=intent.getParcelableExtra(INTENT_KEY_ENTITY)?:DietEntity()

	    foodViewModel=viewModel(FoodViewModel::class.java)
	    foodViewModel

	    searchView.isSubmitButtonEnabled=true
	    val searchAutoComplete = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text) as SearchView.SearchAutoComplete
	    searchAutoComplete.threshold=1
	    searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
		    override fun onQueryTextSubmit(query: String?):Boolean
		    {
			    CLog.d("query=$query")
			    return true
		    }/*=
			    query?.let {
				    val newQuery="%$it%"
				    foodViewModel.find(newQuery)
				    true
			    }?:false*/

		    override fun onQueryTextChange(newText: String?)=
				    newText?.let {
					    ioThread {
						    val query="%$it%"
						    val cursor=foodViewModel.find(query)
						    CLog.d("cursor size=${cursor.count}")
						    if(cursor.count>0)
							    mainThread {
								    searchView.suggestionsAdapter=SearchViewAdapter(this@InsertDietActivity,cursor,searchView)
							    }
					    }
					    true
				    }?:false
	    })
    }

	class SearchViewAdapter(context: Context, c: Cursor,val searchView: SearchView, autoRequery: Boolean=false) : CursorAdapter(context, c, autoRequery) {
		override fun newView(context: Context?, cursor: Cursor?, parent: ViewGroup?)
				=LayoutInflater.from(context).inflate(R.layout.item_searchview_popup,parent,false)

		override fun bindView(view: View, context: Context, cursor: Cursor) {
			CLog.d("name=${cursor.getString(cursor.getColumnIndexOrThrow("name"))}, calorie=${cursor.getFloat(cursor.getColumnIndexOrThrow("calorie")).toString()}")
			view.txtName.text= cursor.getString(cursor.getColumnIndexOrThrow("name"))
			view.txtCalorie.text= cursor.getFloat(cursor.getColumnIndexOrThrow("calorie")).toString()
			view.setOnClickListener {
				searchView.isIconified = true;
			}
		}
	}
}

